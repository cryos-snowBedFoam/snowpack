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
#include <snowpack/vanGenuchten.h>
#include <snowpack/snowpackCore/Snowpack.h>

#include <assert.h>
#include <sstream>
#include <errno.h>

//Eigen
#include <Eigen/Dense>
#include <Eigen/Sparse>
#include <Eigen/IterativeLinearSolvers>
#include <Eigen/SparseQR>
#include <Eigen/SparseCholesky>
#include <Eigen/SparseLU>
#include <Eigen/Core>

#include "omp.h"


typedef Eigen::Triplet<double> Trip;

#ifdef CLAPACK
	// Matching C data types with FORTRAN data types (taken from f2c.h):
	typedef long int integer;
	typedef double doublereal;

	// Declare the function interfaces with the LAPACK library (taken from clapack.h):
	extern "C" {
		/* Subroutine */ int dgesvd_(char *jobu, char *jobvt, integer *m, integer *n,
		doublereal *a, integer *lda, doublereal *s, doublereal *u, integer *
		ldu, doublereal *vt, integer *ldvt, doublereal *work, integer *lwork,
		integer *info);

		/* Subroutine */ int dgesdd_(char *jobz, integer *m, integer *n, doublereal *
		a, integer *lda, doublereal *s, doublereal *u, integer *ldu,
		doublereal *vt, integer *ldvt, doublereal *work, integer *lwork,
		integer *iwork, integer *info);

		/* Subroutine */ int dgtsv_(integer *n, integer *nrhs, doublereal *dl,
		doublereal *d__, doublereal *du, doublereal *b, integer *ldb, integer
		*info);
	}
#endif

using namespace mio;
using namespace std;
using namespace Eigen;

VapourTransport_OF_to_SN::VapourTransport_OF_to_SN(const SnowpackConfig& cfg)
               : WaterTransport(cfg), RichardsEquationSolver1d(cfg, false), variant(),
                 iwatertransportmodel_snow(BUCKET), iwatertransportmodel_soil(BUCKET), watertransportmodel_snow("BUCKET"), watertransportmodel_soil("BUCKET"),
                 sn_dt(IOUtils::nodata),timeStep(IOUtils::nodata),waterVaporTransport_timeStep(IOUtils::nodata),
                 hoar_thresh_rh(IOUtils::nodata), hoar_thresh_vw(IOUtils::nodata), hoar_thresh_ta(IOUtils::nodata),
                 /*hoar_density_buried(IOUtils::nodata), hoar_density_surf(IOUtils::nodata), hoar_min_size_buried(IOUtils::nodata),
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

	//vapour transport model
	cfg.getValue("VAPOUR_TRANSPORT_MODEL", "SnowpackAdvanced", vapour_transport_model);
	if(!(vapour_transport_model =="diffusion_SNOWPACK" || vapour_transport_model =="convection_OpenFOAM" || vapour_transport_model =="NONE"))
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
	// First, check the vapour_transport_model
	if (vapour_transport_model !="convection_OpenFOAM") {
		//std::cout << " I am not convection_OpenFOAM \n";
		return;
	}

	// First, consider no soil with no snow on the ground
	if (!useSoilLayers && Xdata.getNumberOfNodes() == Xdata.SoilNode+1) {
		return;
	}

	// (1) if we do it like the first diffusive paper, we just use ql in vaporTransport not in waterTransport
	// (2) if we use ql first in waterTransport, then the leftover will be used in vaporTransport ....	
	compSurfaceSublimation(Mdata, ql, Xdata, Sdata);
	ql=0;

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
	    WaterTransport::adjustDensity(Xdata);
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
	 
	//if (vapour_transport_model =="convection_OpenFOAM")
	//{		 
		const size_t nN = Xdata.getNumberOfNodes();
		size_t nE = nN-1;
		vector<NodeData>& NDS = Xdata.Ndata;
		vector<ElementData>& EMS = Xdata.Edata;
		size_t e = nE;
		std::vector<double> deltaM(nE, 0.);//Calculate the limited layer mass change		
			
		e = nE;			
		// consider the mass change due to vapour transport in snow/soil
		while (e-- > 0) {
			//const double massPhaseChange = EMS[e].vapTrans_snowDenChangeRate*(EMS[e].L*sn_dt)+deltaM[e];//JJJJJ
			//const double massPhaseChange = EMS[e].vapTrans_snowDenChangeRate*(EMS[e].L*sn_dt);////deltaM[e] is not needed so this can be deleted ...
			const double massPhaseChange = EMS[e].vapTrans_snowDenChangeRate*(EMS[e].L*sn_dt)+deltaM[e];// is not needed so this can be deleted ...

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
		for (e = 0; e < nE; e++)
		{	
			EMS[e].Qmm = 0.0;
			
			if (deltaM[e] < 0.) {
				// Mass loss: apply mass change first to water, then to ice, based on energy considerations
				// We can only do this partitioning here in this "simple" way, without checking if the mass is available, because we already limited dM above, based on available ICE + WATER.
				const double dTh_water = std::max( (EMS[e].VG.theta_r * (1. + Constants::eps) - EMS[e].theta[WATER])  ,  deltaM[e] / (Constants::density_water * EMS[e].L) );
				const double dTh_ice = ( deltaM[e] - (dTh_water * Constants::density_water * EMS[e].L) ) / (Constants::density_ice * EMS[e].L);
				EMS[e].theta[WATER] += dTh_water;
				EMS[e].theta[ICE] += dTh_ice;

				Sdata.mass[SurfaceFluxes::MS_SUBLIMATION] += dTh_water * Constants::density_water * EMS[e].L;
				Sdata.mass[SurfaceFluxes::MS_SUBLIMATION] += dTh_ice * Constants::density_ice * EMS[e].L;
				EMS[e].M += dTh_water * Constants::density_water * EMS[e].L+dTh_ice * Constants::density_ice * EMS[e].L;
				assert(EMS[e].M >= (-Constants::eps2)); //mass must be positive

				EMS[e].Qmm += (dTh_water*Constants::density_water*Constants::lh_vaporization +
								dTh_ice*Constants::density_ice*Constants::lh_sublimation)/sn_dt;//[w/m^3]

				// If present at surface, surface hoar is sublimated away
				if (e == nE-1 && deltaM[e]<0) {
					dHoar = std::max(-NDS[nN-1].hoar, deltaM[e]);
				}
			} else {		// Mass gain: add water in case temperature at or above melting point, ice otherwise
				if (EMS[e].Te >= EMS[e].meltfreeze_tk) {
					EMS[e].theta[WATER] += deltaM[e] / (Constants::density_water * EMS[e].L);
					EMS[e].Qmm += (deltaM[e]*Constants::lh_vaporization)/sn_dt/EMS[e].L;//  [w/m^3]
					Sdata.mass[SurfaceFluxes::MS_SUBLIMATION] += deltaM[e]; //
				} else {
					EMS[e].theta[ICE] += deltaM[e] / (Constants::density_ice * EMS[e].L);
					EMS[e].Qmm += (deltaM[e]*Constants::lh_sublimation)/sn_dt/EMS[e].L;// [w/m^3]
					Sdata.mass[SurfaceFluxes::MS_SUBLIMATION] += deltaM[e]; //
				}
				EMS[e].M += deltaM[e];
				assert(EMS[e].M >= (-Constants::eps2)); //mass must be positive
			}

			
			EMS[e].theta[AIR] = std::max(1. - EMS[e].theta[WATER] - EMS[e].theta[WATER_PREF] - EMS[e].theta[ICE] - EMS[e].theta[SOIL],0.);
			if(std::fabs(EMS[e].theta[AIR])<1.e-15)
			{
				EMS[e].theta[AIR]=0;
			}
			EMS[e].updDensity();
			assert(EMS[e].Rho > 0 || EMS[e].Rho==IOUtils::nodata); //density must be positive
			if (!(EMS[e].Rho > Constants::eps && EMS[e].theta[AIR] >= 0. && EMS[e].theta[WATER] <= 1.+Constants::eps && EMS[e].theta[ICE] <= 1.+Constants::eps)) {
					prn_msg(__FILE__, __LINE__, "err", Date(),
						"Volume contents: e=%d nE=%d rho=%lf ice=%lf wat=%lf wat_pref=%lf soil=%lf air=%le", e, nE, EMS[e].Rho, EMS[e].theta[ICE],
							EMS[e].theta[WATER], EMS[e].theta[WATER_PREF], EMS[e].theta[SOIL], EMS[e].theta[AIR]);
					throw IOException("Cannot evaluate mass balance in vapour transport LayerToLayer routine", AT);
			}
			
			/*
			// Numerical rounding errors were found to lead to theta[AIR] < 0, so force the other components between [0,1]:
			EMS[e].theta[ICE] = std::max(0., std::min(1. - EMS[e].theta[SOIL], EMS[e].theta[ICE]));
			EMS[e].theta[WATER] = std::max(0., std::min(1. - EMS[e].theta[SOIL], EMS[e].theta[WATER]));
			EMS[e].theta[WATER_PREF] = std::max(0., std::min(1., EMS[e].theta[WATER_PREF]));
			// Update theta[AIR] and density:
			EMS[e].theta[AIR] = (1. - EMS[e].theta[WATER] - EMS[e].theta[WATER_PREF] - EMS[e].theta[ICE] - EMS[e].theta[SOIL]);		
			EMS[e].updDensity();
			assert(EMS[e].Rho > 0 || EMS[e].Rho==IOUtils::nodata); //density must be positive
			if (!(EMS[e].Rho > Constants::eps && EMS[e].theta[AIR] >= 0.)) {
				if(EMS[e].theta[AIR] > -Constants::eps2) {
					EMS[e].theta[AIR] = 0.;
				} else {
					prn_msg(__FILE__, __LINE__, "err", Date(),
						"Volume contents: e=%d nE=%d rho=%lf ice=%lf wat=%lf wat_pref=%le air=%le  soil=%le", e, nE, EMS[e].Rho, EMS[e].theta[ICE], EMS[e].theta[WATER], EMS[e].theta[WATER_PREF], EMS[e].theta[AIR], EMS[e].salinity);
					throw IOException("Cannot evaluate mass balance in vapour transport LayerToLayer routine", AT);
				}
			}			
			*/
			
			//some useful output in case of vapor transport
			//EMS[e].vapTrans_underSaturationDegree = this can be updated in OpenFOAM solver
			EMS[e].vapTrans_cumulativeDenChange += deltaM[e]/EMS[e].L;
			//EMS[e].vapTrans_snowDenChangeRate = deltaM[e]/EMS[e].L/sn_dt; // this was updated by OF			
		}

		Sdata.hoar += dHoar;
		NDS[nN-1].hoar += dHoar;
		if (NDS[nN-1].hoar < 0.) {
			NDS[nN-1].hoar = 0.;
		}
		
	//}
}

/**
 * @brief Calculate the surface sublimation / deposition (i.e., only gas-solid). \n
 * The fraction of the latent heat flux ql that has not been used so far will be used for
 * sublimation/deposition. If positive (and above a certain cutoff level) then there
 * is a possibility that surface hoar crystal have grown. Of course, if negative
 * then we are also loosing mass from the surface.\n
 * This function additionally takes care of surface hoar formation and destruction.
 * Note that surface hoar is a nodal property, altough the corresponding mass is carried
 * by the underlying element.
 * @param *Mdata
 * @param ql Latent heat flux (W m-2)
 * @param *Xdata
 * @param *Sdata
 */
void VapourTransport_OF_to_SN::compSurfaceSublimation(const CurrentMeteo& Mdata, double& ql, SnowStation& Xdata, SurfaceFluxes& Sdata)
{
	double dL = 0., dM = 0.;     // Length and mass changes
	double M = 0.;               // Initial mass and volumetric content (water or ice)
	double dHoar = 0.;           // Actual change in hoar mass
	double cH_old;               // Temporary variable to hold height of snow

	const size_t nN = Xdata.getNumberOfNodes();
	size_t nE = nN-1;
	vector<NodeData>& NDS = Xdata.Ndata;
	vector<ElementData>& EMS = Xdata.Edata;
	const double Tss = NDS[nE].T; // Surface Temperature

	/*
	 * If ql > 0:
	 * Surface hoar is formed when surface temperature is below freezing.
	 * If no surface hoar can be formed, ql is kept and is used as boundary condition
	 * when calculating vapour flux.
	 * If there are elements and ql < 0:
	 * If ql is large enough to remove full surface elements, remove them.
	 * left over ql is used as boundary condition when calculating vapour flux.
	 *
	 * In both cases: add/subtract mass to MS_SUBLIMATION
	 */
	if (ql > Constants::eps2) { // Add Mass
		const double meltfreeze_tk = (Xdata.getNumberOfElements()>0)? Xdata.Edata[Xdata.getNumberOfElements()-1].meltfreeze_tk : Constants::meltfreeze_tk;
		if (Tss < meltfreeze_tk) { // Add Ice
			dM = ql*sn_dt/Constants::lh_sublimation;
			//if rh is very close to 1, vw too high or ta too high, surface hoar is destroyed and should not be formed
			if (!((Mdata.rh > hoar_thresh_rh) || (Mdata.vw > hoar_thresh_vw) || (Mdata.ta >= IOUtils::C_TO_K(hoar_thresh_ta)))) {
				// Under these conditions, form surface hoar
				ql = 0.;
				Sdata.mass[SurfaceFluxes::MS_SUBLIMATION] += dM;
				dHoar = dM;

				// In this case adjust properties of element, keeping snow density constant
				const double L_top = EMS[nE-1].L;
				const double theta_i0 = EMS[nE-1].theta[ICE];
				dL = dM/(EMS[nE-1].Rho); // length change
				if (nE == Xdata.SoilNode) {
					dL = 0.;
					dM = std::min(dM,EMS[nE-1].theta[AIR]*(Constants::density_ice*EMS[nE-1].L));
				}
				NDS[nE].z += dL + NDS[nE].u; NDS[nE].u = 0.0;
				EMS[nE-1].L0 = EMS[nE-1].L = L_top + dL;
				EMS[nE-1].E = EMS[nE-1].Eps = EMS[nE-1].dEps = EMS[nE-1].Eps_e = EMS[nE-1].Eps_v = EMS[nE-1].S = 0.0;
				EMS[nE-1].theta[ICE] *= L_top/EMS[nE-1].L;
				EMS[nE-1].theta[ICE] += dM/(Constants::density_ice*EMS[nE-1].L);
				EMS[nE-1].theta[ICE] = std::max(0., std::min(1., EMS[nE-1].theta[ICE]));
				EMS[nE-1].theta[WATER] *= L_top/EMS[nE-1].L;
				EMS[nE-1].theta[WATER] = std::max(0., std::min(1., EMS[nE-1].theta[WATER]));
				EMS[nE-1].theta[WATER_PREF] *= L_top/EMS[nE-1].L;
				EMS[nE-1].theta[WATER_PREF] = std::max(0., std::min(1., EMS[nE-1].theta[WATER_PREF]));

				for (size_t ii = 0; ii < Xdata.number_of_solutes; ii++) {
					EMS[nE-1].conc[ICE][ii] *= L_top*theta_i0/(EMS[nE-1].theta[ICE]*EMS[nE-1].L);
				}

				EMS[nE-1].M += dM;
				assert(EMS[nE-1].M >= (-Constants::eps2)); //mass must be positive

				// Update remaining volumetric contents and density
				EMS[nE-1].theta[AIR] = std::max(0., 1.0 - EMS[nE-1].theta[WATER] - EMS[nE-1].theta[WATER_PREF] - EMS[nE-1].theta[ICE] - EMS[nE-1].theta[SOIL]);
				EMS[nE-1].updDensity();
			}
		}
	} else if ((ql < (-Constants::eps2)) && (nE > 0)) {
		// If ql < 0, SUBLIMATE mass off
		std::vector<double> M_Solutes(Xdata.number_of_solutes, 0.); // Mass of solutes from disappearing phases
		size_t e = nE;
		while ((e > 0) && (ql < (-Constants::eps2))) {  // While energy is available
			e--;
			/*
			* Determine the amount of potential sublimation and collect some variables
			* that will be continuously used: L0 and M
			*/
			const double L0 = EMS[e].L;
			const double theta_i0 = EMS[e].theta[ICE];
			M = theta_i0*Constants::density_ice*L0;
			dM = ql*sn_dt/Constants::lh_sublimation;
			if (-dM > M) {
				// Only if mass change is sufficient to remove the full element
				dM = -M;
				// Add solutes to Storage
				for (size_t ii = 0; ii < Xdata.number_of_solutes; ii++) {
					M_Solutes[ii] += EMS[e].conc[ICE][ii]*theta_i0*L0;
				}
				EMS[e].theta[ICE] = 0.;
				dL = 0.;

				EMS[e].M += dM;
				Sdata.mass[SurfaceFluxes::MS_SUBLIMATION] += dM;
				ql -= dM*Constants::lh_sublimation/sn_dt;     // Update the energy used

				// If present at surface, surface hoar is sublimated away
				if (e == nE-1) {
					dHoar = std::max(-NDS[nN-1].hoar, dM);
				}

				// Update remaining volumetric contents and density
				EMS[e].theta[AIR] = std::max(0., 1.0 - EMS[e].theta[WATER] - EMS[e].theta[WATER_PREF] - EMS[e].theta[ICE] - EMS[e].theta[SOIL]);
				EMS[e].updDensity();
				// Merge the element if it is a snow layer. This will take care of possible left over liquid water (will be put one layer down)
				// Keep layer if it is a soil layer inside the snowpack (for example with snow farming)
				if(e>=Xdata.SoilNode) {
					if(EMS[e].theta[SOIL]<Constants::eps) {
						if (e>0) SnowStation::mergeElements(EMS[e-1], EMS[e], false, true);
						// Now reduce the number of elements by one.
						nE--;
					}
					//In case e==Xdata.SoilNode, we removed the last snow element and we should break out of the loop.
					if(e==Xdata.SoilNode) break;
				}
			} else {
				// Not enough energy anymore to remove complete element, so we should break out of the loop.
				break;
			}

			//check that thetas and densities are consistent
			assert(EMS[e].theta[SOIL] >= (-Constants::eps2) && EMS[e].theta[SOIL] <= (1.+Constants::eps2));
			assert(EMS[e].theta[ICE] >= (-Constants::eps2) && EMS[e].theta[ICE]<=(1.+Constants::eps2));
			assert(EMS[e].theta[WATER] >= (-Constants::eps2) && EMS[e].theta[WATER]<=(1.+Constants::eps2));
			assert(EMS[e].theta[WATER_PREF] >= (-Constants::eps2) && EMS[e].theta[WATER_PREF]<=(1.+Constants::eps2));
			assert(EMS[e].theta[AIR] >= (-Constants::eps2) && EMS[e].theta[AIR]<=(1.+Constants::eps2));
			assert(EMS[e].Rho >= (-Constants::eps2) || EMS[e].Rho==IOUtils::nodata); //we want positive density
		}

		// Now take care of left over solute mass.
		if (nE == Xdata.SoilNode) { // Add Solute Mass to Runoff TODO HACK CHECK
			for (size_t ii = 0; ii < Xdata.number_of_solutes; ii++) {
				Sdata.load[ii] += M_Solutes[ii]/S_TO_H(sn_dt);
			}
		} else { // Add Solute Mass to Element below
			if (EMS[e].theta[WATER] > 0.) {
				for(size_t ii = 0; ii < Xdata.number_of_solutes; ii++) {
					EMS[e].conc[WATER][ii] += M_Solutes[ii]/EMS[e].theta[WATER]/EMS[e].L;
				}
			} else if (EMS[e].theta[ICE] > 0.) {
				for (size_t ii = 0; ii < Xdata.number_of_solutes; ii++) {
					EMS[e].conc[ICE][ii] += M_Solutes[ii]/EMS[e].theta[ICE]/EMS[e].L;
				}
			} else {
				for (size_t ii = 0; ii < Xdata.number_of_solutes; ii++) {
					EMS[e].conc[SOIL][ii] += M_Solutes[ii]/EMS[e].theta[SOIL]/EMS[e].L;
				}
			}
		}
		Xdata.reduceNumberOfElements(nE);
	}

	// HACK: this code is under verification. The comment reads "surface hoar *is* destroyed, but the next line says surface hoar *may be* destroyed, depending on the sign of the latent heat flux.
	// If the code is correct, we can delete this part, if the comment is correct, we should modify the code to read: hoar = -NDS[nE].hoar;
	// Check for surface hoar destruction or formation (once upon a time ml_sn_SurfaceHoar)
	/*if ((Mdata.rh > hoar_thresh_rh) || (Mdata.vw > hoar_thresh_vw) || (Mdata.ta >= IOUtils::C_TO_K(hoar_thresh_ta))) {
		//if rh is very close to 1, vw too high or ta too high, surface hoar is destroyed
		hoar = std::min(hoar, 0.);
	}*/

	Sdata.hoar += dHoar;
	NDS[nN-1].hoar += dHoar;
	if (NDS[nN-1].hoar < 0.) {
		NDS[nN-1].hoar = 0.;
	}

	// Surface hoar cannot exist when the top element is wet
	if (nE > 0) {
		const double theta_r=((iwatertransportmodel_snow==RICHARDSEQUATION && nE-1>=Xdata.SoilNode) || (iwatertransportmodel_soil==RICHARDSEQUATION && nE-1<Xdata.SoilNode)) ? (PhaseChange::RE_theta_r) : (PhaseChange::theta_r);
		if (Xdata.Edata[nE-1].theta[WATER] > theta_r) {
			NDS[nE].hoar = 0.;
		}
	}

	// At the end also update the overall height
	cH_old = Xdata.cH;
	Xdata.cH = NDS[Xdata.getNumberOfNodes()-1].z + NDS[Xdata.getNumberOfNodes()-1].u;
	if (Xdata.mH!=Constants::undefined) Xdata.mH -= std::min(Xdata.mH - Xdata.Ground, (cH_old - Xdata.cH));	// TODO/HACK: why is this correction for Xdata.mH necessary?
}
/*
 * End of VapourTransport_OF_to_SN.cc
 */
