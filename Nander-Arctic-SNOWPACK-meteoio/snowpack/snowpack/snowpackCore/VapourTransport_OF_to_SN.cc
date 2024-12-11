/*
 *  SNOWPACK stand-alone
 *
 *  Copyright WSL Institute for Snow and Avalanche Research SLF, DAVOS, SWITZERLAND
*/
/*  This file is part of Snowpack.
	Snowpack is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	Snowpack is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with Snowpack.  If not, see <http://www.gnu.org/licenses/>.
*/


#include <snowpack/snowpackCore/VapourTransport_OF_to_SN.h>
#include <snowpack/snowpackCore/VapourTransport.h>
#include <snowpack/snowpackCore/WaterTransport.h>
#include <snowpack/vanGenuchten.h>
#include <snowpack/snowpackCore/Snowpack.h>
#include <snowpack/Constants.h>

// MeteoIO constants
#include <meteoio/meteoLaws/Meteoconst.h>

#include <assert.h>
#include <sstream>
#include <errno.h>


//Eigen, note we temporarily disable Effective C++ warnings
#pragma GCC diagnostic push
#pragma GCC diagnostic ignored "-Weffc++"
#pragma GCC diagnostic ignored "-Wctor-dtor-privacy"
#include <meteoio/thirdParty/Eigen/Dense>
#include <meteoio/thirdParty/Eigen/Sparse>
#include <meteoio/thirdParty/Eigen/IterativeLinearSolvers>
#include <meteoio/thirdParty/Eigen/SparseQR>
#include <meteoio/thirdParty/Eigen/SparseCholesky>
#include <meteoio/thirdParty/Eigen/SparseLU>
#include <meteoio/thirdParty/Eigen/Core>

typedef Eigen::Triplet<double> Trip;
#pragma GCC diagnostic pop

using namespace mio;
using namespace std;
using namespace Eigen;

VapourTransport_OF_to_SN::VapourTransport_OF_to_SN(const SnowpackConfig& cfg)
               : WaterTransport(cfg), RichardsEquationSolver1d(cfg, false), variant(),
                 iwatertransportmodel_snow(BUCKET), iwatertransportmodel_soil(BUCKET), watertransportmodel_snow("BUCKET"), watertransportmodel_soil("BUCKET"),
                 sn_dt(IOUtils::nodata),timeStep(IOUtils::nodata),waterVaporTransport_timeStep(IOUtils::nodata),
                 hoar_thresh_rh(IOUtils::nodata), hoar_thresh_vw(IOUtils::nodata), hoar_thresh_ta(IOUtils::nodata),
                 /*hoar_density_buried(IOUtils::nodata), hoar_density_surf(IOUtils::nodata), hoar_min_size_buried(IOUtils::nodata), enable_vapour_transport(false),
                 minimum_l_element(IOUtils::nodata),*/ useSoilLayers(false), water_layer(false), vapour_transport_model("NONE"),
                 height_of_meteo_values(0.), adjust_height_of_meteo_values(true)
{
	cfg.getValue("VARIANT", "SnowpackAdvanced", variant);

	// Defines whether soil layers are used
	cfg.getValue("SNP_SOIL", "Snowpack", useSoilLayers);

	//To build a thin top rain-water layer over a thin top ice layer, rocks, roads etc.
	cfg.getValue("WATER_LAYER", "SnowpackAdvanced", water_layer);

	/**
	 * @brief No surface hoar will form for rH above threshold (1)
	 * - Original calibration with the 98/99 data set: 0.9
	 * - r141: HOAR_THRESH_RH set to 0.9
	 * - r719: HOAR_THRESH_RH set to 0.97
	 */
	cfg.getValue("HOAR_THRESH_RH", "SnowpackAdvanced", hoar_thresh_rh);

	/**
	 * @brief No surface hoar will form at wind speeds above threshold (m s-1)
	 * - Original calibration with the 98/99 data set: 3.5
	 * - r141: HOAR_THRESH_VW set to 3.0
	 * - r242: HOAR_THRESH_VW set to 3.5
	 */
	cfg.getValue("HOAR_THRESH_VW", "SnowpackAdvanced", hoar_thresh_vw);

	/**
	 * @brief No surface hoar will form at air temperatures above threshold (m s-1)
	 * - Originaly, using THRESH_RAIN
	 * - r787: HOAR_THRESH_TA set to 1.2
	 */
	cfg.getValue("HOAR_THRESH_TA", "SnowpackAdvanced", hoar_thresh_ta);

	//Calculation time step in seconds as derived from CALCULATION_STEP_LENGTH
	const double calculation_step_length = cfg.get("CALCULATION_STEP_LENGTH", "Snowpack");
	sn_dt = M_TO_S(calculation_step_length);

	//Vapour transport settings
	cfg.getValue("ENABLE_VAPOUR_TRANSPORT", "SnowpackAdvanced", enable_vapour_transport);

	//vapour transport model
	cfg.getValue("VAPOUR_TRANSPORT_MODEL", "SnowpackAdvanced", vapour_transport_model);
	if(!(vapour_transport_model =="diffusion_SNOWPACK" || vapour_transport_model =="convection_OpenFOAM"))
	{
		throw IOException("VAPOUR_TRANSPORT_MODEL must be diffusion_SNOWPACK or convection_OpenFOAM or NONE", AT);
	}

	//reading diffusionScalingFactor_
	//cfg.getValue("DIFFUSION_SCALING_FACTOR", "SnowpackAdvanced", diffusionScalingFactor_);

	//Water transport model snow
	cfg.getValue("WATERTRANSPORTMODEL_SNOW", "SnowpackAdvanced", watertransportmodel_snow);
	iwatertransportmodel_snow=UNDEFINED;
	if (watertransportmodel_snow=="BUCKET") {
		iwatertransportmodel_snow=BUCKET;
	} else if (watertransportmodel_snow=="NIED") {
		iwatertransportmodel_snow=NIED;
	} else if (watertransportmodel_snow=="RICHARDSEQUATION") {
		iwatertransportmodel_snow=RICHARDSEQUATION;
	}

	//Water transport model soil
	cfg.getValue("WATERTRANSPORTMODEL_SOIL", "SnowpackAdvanced", watertransportmodel_soil);
	iwatertransportmodel_soil=UNDEFINED;
	if (watertransportmodel_soil=="BUCKET") {
		iwatertransportmodel_soil=BUCKET;
	} else if (watertransportmodel_soil=="NIED") {
		iwatertransportmodel_soil=NIED;
	} else if (watertransportmodel_soil=="RICHARDSEQUATION") {
		iwatertransportmodel_soil=RICHARDSEQUATION;
	}

	cfg.getValue("HEIGHT_OF_METEO_VALUES", "Snowpack", height_of_meteo_values);
	cfg.getValue("ADJUST_HEIGHT_OF_METEO_VALUES", "SnowpackAdvanced", adjust_height_of_meteo_values);

}

void VapourTransport_OF_to_SN::compTransportMass(const CurrentMeteo& Mdata, double& ql,
                                       SnowStation& Xdata, SurfaceFluxes& Sdata)
{
	// if vapor transpot is not enabled, it does not matter which model is set and it does not continue in this function
	if (!enable_vapour_transport) {
		return;
	}

	// check the vapour_transport_model
	if (vapour_transport_model !="convection_OpenFOAM") {
		return;
	}

	// First, consider no soil with no snow on the ground
	if (!useSoilLayers && Xdata.getNumberOfNodes() == Xdata.SoilNode+1) {
		return;
	}


    /////////////////////////OF-SN coupleing///////////////////////////
    for(size_t i=0; i<Xdata.getNumberOfElements(); i++)
	{
		Xdata.Edata[i].vapTrans_deltaTheta_ice=0.0;
		Xdata.Edata[i].vapTrans_deltaTheta_ice=0.0;
	}
	for(size_t i=Xdata.SoilNode; i<Xdata.getNumberOfElements(); i++)
	{
		Xdata.Edata[i].vapTrans_deltaTheta_ice-=Xdata.Edata[i].theta[ICE];
		Xdata.Edata[i].vapTrans_deltaTheta_ice-=Xdata.Edata[i].theta[WATER];
	}
	/////////////////////////OF-SN coupleing///////////////////////////
		
    try {
	    LayerToLayer(Mdata, Xdata, Sdata, ql);
	    WaterTransport::adjustDensity(Xdata, Sdata);
	    //WaterTransport::mergingElements(Xdata, Sdata);
    } catch(const exception&)
    {
	    prn_msg( __FILE__, __LINE__, "err", Mdata.date, "Error in transportVapourMass()");
	    throw;
    }
    
	/////////////////////////OF-SN coupleing///////////////////////////
	for(size_t i=Xdata.SoilNode; i<Xdata.getNumberOfElements(); i++)
	{
		Xdata.Edata[i].vapTrans_deltaTheta_ice+=Xdata.Edata[i].theta[ICE];
		Xdata.Edata[i].vapTrans_deltaTheta_ice+=Xdata.Edata[i].theta[WATER];
	}
	/////////////////////////OF-SN coupleing///////////////////////////

}

void VapourTransport_OF_to_SN::LayerToLayer(const CurrentMeteo& Mdata, SnowStation& Xdata, SurfaceFluxes& Sdata, double& ql)
 {
	const size_t nN = Xdata.getNumberOfNodes();
	size_t nE = nN-1;
	vector<NodeData>& NDS = Xdata.Ndata;
	vector<ElementData>& EMS = Xdata.Edata;
	size_t e = nE;
	std::vector<double> deltaM(nE, 0.);//Calculate the limited layer mass change		
		
	e = nE;			
	// consider the mass change due to vapour transport in snow/soil
	while (e-- > 0) {
		const double massPhaseChange = EMS[e].vapTrans_snowDenChangeRate*(EMS[e].L*sn_dt)+deltaM[e];

		double dM = 0.;	//mass change induced by vapor flux (kg m-2)

		// Now, the mass change is limited by:
		// - we cannot remove more WATER and ICE than available
		// - we cannot add more WATER and ICE than pore space available
		if ( EMS[e].theta[SOIL] < Constants::eps ) {// there is no soil in element to keep element not to merge
			dM = std::max(  -((EMS[e].theta[WATER] - EMS[e].VG.theta_r * (1. + Constants::eps)) * Constants::density_water * EMS[e].L + (EMS[e].theta[ICE] - Snowpack::min_ice_content) * Constants::density_ice * EMS[e].L)  ,
							std::min(  (EMS[e].theta[AIR] * Constants::density_ice * EMS[e].L), massPhaseChange  )
				 ); // mass change due to difference in water vapor flux (kg m-2), at most can fill the pore space.
		} else {

			dM = std::max(  -((EMS[e].theta[WATER] - EMS[e].VG.theta_r * (1. + Constants::eps)) * Constants::density_water * EMS[e].L + EMS[e].theta[ICE] * Constants::density_ice * EMS[e].L)  ,
							std::min(  (EMS[e].theta[AIR] * Constants::density_ice * EMS[e].L), massPhaseChange  )
				 ); // mass change due to difference in water vapor flux (kg m-2), at most can fill the pore space.

		}
		

		// If there is no pore space, or, in fact, only so much pore space to accomodate the larger volume occupied by ice when all water freezes,
		// we inhibit vapour flux. This is necessary to maintain saturated conditions when present, and this is in turn necessary for the stability in the Richards equation solver.
		if(EMS[e].theta[AIR] < EMS[e].theta[WATER]*(Constants::density_water/Constants::density_ice - 1.) + Constants::eps) {
			dM = 0.;
		}

		deltaM[e] = dM;
	}
		
	double dHoar = 0.;
	for (size_t e = 0; e < nE; e++) {
		EMS[e].Qmm = 0.0;
		
		if (deltaM[e] < 0.) {
			// Mass loss: apply mass change first to water, then to ice, based on energy considerations
			// We can only do this partitioning here in this "simple" way, without checking if the mass is available, because we already limited dM above, based on available ICE + WATER.
			const double dTh_water = std::max((EMS[e].VG.theta_r * (1. + Constants::eps) - EMS[e].theta[WATER]),
											  deltaM[e] / (Constants::density_water * EMS[e].L));
			const double dTh_ice = std::max(-EMS[e].theta[ICE], ( deltaM[e] - (dTh_water * Constants::density_water * EMS[e].L) ) / (Constants::density_ice * EMS[e].L));
			EMS[e].theta[WATER] += dTh_water;
			EMS[e].theta[ICE] += dTh_ice;

			Sdata.mass[SurfaceFluxes::MS_EVAPORATION] += dTh_water * Constants::density_water * EMS[e].L;
			Sdata.mass[SurfaceFluxes::MS_SUBLIMATION] += dTh_ice * Constants::density_ice * EMS[e].L;
			EMS[e].M += dTh_water * Constants::density_water * EMS[e].L+dTh_ice * Constants::density_ice * EMS[e].L;
			assert(EMS[e].M >= (-Constants::eps2)); //mass must be positive

			EMS[e].Qmm += (dTh_water * Constants::density_water * Constants::lh_vaporization
						   + dTh_ice * Constants::density_ice * Constants::lh_sublimation
						  ) / sn_dt; // [w/m^3]

			// If present at surface, surface hoar is sublimated away
			if (e == nE-1 && deltaM[e]<0) {
				dHoar = std::max(-NDS[nN-1].hoar, deltaM[e]);
			}
		} else {		// Mass gain: add water in case temperature at or above melting point, ice otherwise
			if (EMS[e].Te >= EMS[e].meltfreeze_tk) {
				EMS[e].theta[WATER] += deltaM[e] / (Constants::density_water * EMS[e].L);
				EMS[e].Qmm += (deltaM[e]*Constants::lh_vaporization)/sn_dt/EMS[e].L;//  [w/m^3]
				Sdata.mass[SurfaceFluxes::MS_EVAPORATION] += deltaM[e];
			} else {
				EMS[e].theta[ICE] += deltaM[e] / (Constants::density_ice * EMS[e].L);
				EMS[e].Qmm += (deltaM[e]*Constants::lh_sublimation)/sn_dt/EMS[e].L;// [w/m^3]
				Sdata.mass[SurfaceFluxes::MS_SUBLIMATION] += deltaM[e]; //
			}
			EMS[e].M += deltaM[e];
			assert(EMS[e].M >= (-Constants::eps2)); //mass must be positive
		}

		
		EMS[e].theta[AIR] = std::max(1. - EMS[e].theta[WATER] - EMS[e].theta[WATER_PREF] - EMS[e].theta[ICE] - EMS[e].theta[SOIL],0.);
		if (std::fabs(EMS[e].theta[AIR]) < 1.e-15) {
			EMS[e].theta[AIR]=0;
		}
		EMS[e].updDensity();
		assert(EMS[e].Rho > 0 || EMS[e].Rho==IOUtils::nodata); //density must be positive
		if (!(EMS[e].Rho > Constants::eps
		      && EMS[e].theta[AIR] >= 0. && EMS[e].theta[ICE] >= 0. && EMS[e].theta[WATER] >= 0.
		      && EMS[e].theta[WATER] <= 1. + Constants::eps && EMS[e].theta[ICE] <= 1. + Constants::eps
		      && (EMS[e].theta[WATER] + EMS[e].theta[WATER_PREF] + EMS[e].theta[ICE] + EMS[e].theta[SOIL] + EMS[e].theta[AIR] - 1.) < 1.e-12)) {
				prn_msg(__FILE__, __LINE__, "err", Date(),
					"Volume contents: e=%d nE=%d rho=%lf ice=%lf wat=%lf wat_pref=%lf soil=%lf air=%le", e, nE, EMS[e].Rho, EMS[e].theta[ICE],
						EMS[e].theta[WATER], EMS[e].theta[WATER_PREF], EMS[e].theta[SOIL], EMS[e].theta[AIR]);
				throw IOException("Cannot evaluate mass balance in vapour transport LayerToLayer routine", AT);
		}
		
		EMS[e].vapTrans_cumulativeDenChange += deltaM[e]/EMS[e].L;
	}

	Sdata.hoar += dHoar;
	NDS[nN-1].hoar += dHoar;
	if (NDS[nN-1].hoar < 0.) {
		NDS[nN-1].hoar = 0.;
	}		
}
/*
 * End of VapourTransport_OF_to_SN.cc
 */
