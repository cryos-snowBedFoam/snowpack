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
/**
 * @file VapourTransport.h
 */

#ifndef VAPOURTRANSPORT_H
#define VAPOURTRANSPORT_H

#include <snowpack/Constants.h>
#include <snowpack/DataClasses.h>
#include <snowpack/Laws_sn.h>
#include <snowpack/snowpackCore/ReSolver1d.h>
#include <snowpack/snowpackCore/WaterTransport.h>
#include <snowpack/snowpackCore/Snowpack.h>
#include <snowpack/snowpackCore/PhaseChange.h>
#include <snowpack/Meteo.h>
#include <snowpack/Utils.h>
#include <snowpack/snowpackCore/Solver.h>
#include <snowpack/Constants.h>
#include <snowpack/Laws_sn.h>
#include <snowpack/SnowDrift.h>
#include <snowpack/snowpackCore/Metamorphism.h>

#include <meteoio/MeteoIO.h>


/**
 * @class VapourTransport
 * @version 1.0
 * @brief This module contains water vapour transport routines for the 1d snowpack model
 */
class VapourTransport : public WaterTransport {
	public:
		VapourTransport(const SnowpackConfig& cfg);
		void compTransportMass(const CurrentMeteo& Mdata, double& ql, SnowStation& Xdata, SurfaceFluxes& Sdata, const double& surfaceVaporPressure);

	private:

		bool compDensityProfile(const CurrentMeteo& Mdata, SnowStation& Xdata,
                              const bool& ThrowAtNoConvergence, double& ql, const double& surfaceVaporPressure, std::vector<double>& hm_,std::vector<double>& as_, const std::vector<double>& D_el, std::vector<double>& oldVaporDenNode);
		void compSurfaceSublimation(const CurrentMeteo& Mdata, double& ql, SnowStation& Xdata, SurfaceFluxes& Sdata);
		void LayerToLayer(const CurrentMeteo& Mdata, SnowStation& Xdata, SurfaceFluxes& Sdata, double& ql, const double& surfaceVaporPressure);
		double dRhov_dT(const double Tem);

		ReSolver1d RichardsEquationSolver1d;

		std::string variant;

		watertransportmodels iwatertransportmodel_snow, iwatertransportmodel_soil;

		std::string watertransportmodel_snow;
		std::string watertransportmodel_soil;
		double sn_dt,waterVaporTransport_timeStep;
		double timeStep, mint, maxt;
		double hoar_thresh_rh, hoar_thresh_vw, hoar_thresh_ta;
		//double hoar_density_buried, hoar_density_surf, hoar_min_size_buried;
		//double minimum_l_element;
		bool useSoilLayers, water_layer;

		bool enable_vapour_transport,enable_vapour_transport_soil,waterVaporTransport_timeStepAdjust;
		
		double diffusionScalingFactor_,height_of_meteo_values, f;
		
		bool adjust_height_of_meteo_values;
		
		std::string vapour_transport_model;		
};
#endif // End of VapourTransport.h}
