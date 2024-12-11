#ifndef COUPLER_SNOWPACK_OPENFOAM_H
#define COUPLER_SNOWPACK_OPENFOAM_H

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
#include <snowpack/libsnowpack.h>
#include <meteoio/MeteoIO.h>

#include <iostream>
#include <string>
#include <sstream>
#include <ctime>


//#ifdef _MSC_VER
	/*
	This software contains code under BSD license (namely, getopt for Visual C++).
	Therefore, this product includes software developed by the University of
	California, Berkeley and its contributors when compiling with Visual C++.
	*/
//	#include "getopt.h"
//#else
	//#include <unistd.h> //for getopt
//	#include <getopt.h> //for getopt_long
//#endif
#include "Pstream.H"

namespace coupler {

using namespace std;
using namespace mio;

#ifdef DEBUG_ARITHM
	#ifndef _GNU_SOURCE
		#define _GNU_SOURCE
	#endif
	#ifndef __USE_GNU
		#define __USE_GNU
	#endif
	#include <fenv.h>
#endif


/**
 * @class Slope a C. Fierz class ;-)
 */
class Slope {

	public:
		Slope(const mio::Config& cfg);

		double prevailing_wind_dir;
		unsigned int nSlopes;
		unsigned int mainStation;  ///< main station, flat field or slope
		unsigned int sector;       ///< main station (0) or current slope sector (1:nSlopes)
		unsigned int first;        ///< first virtual slope station in computing sequence
		unsigned int luv;
		unsigned int lee;
		bool north, south;
		std::string snow_erosion;
		bool mainStationDriftIndex;
		bool snow_redistribution, luvDriftIndex;

		unsigned int getSectorDir(const double& dir_or_expo) const;
		void setSlope(const unsigned int slope_sequence, vector<SnowStation>& vecXdata, double& wind_dir);

	private:
		double sector_width;       ///< width of slope sector: 360./std::max((unsigned)1, nSlopes-1) deg
};

/**
 * @class Cumsum a C. Fierz class ;-)
 * To cumulate various mass fluxes over either hazard or time series time steps
 */
class Cumsum {

	public:
		Cumsum(const unsigned int nSlopes);

		double precip;
		double drift, snow, runoff, rain;
		double dhs_corr, mass_corr; // inflate/deflate variables
		vector<double> erosion; // Cumulated eroded mass; dumped to file as rate
		vector<double> erosion_length; // Cumulated eroded length
		vector<double> redeposition; // Cumulated eroded mass
		vector<double> redeposition_length; // Cumulated redeposited length
};

/************************************************************
 * static section                                           *
 ************************************************************/

//Global variables in this file:
static string cfgfile = "./SNOWPACK/cfg/io.ini";
static string mode = "RESEARCH";
bool restart = false;
static mio::Date dateBegin, dateEnd;
static vector<string> vecStationIDs;

/// @brief Main control parameters
struct MainControl
{
	size_t nStep;        ///< Time step number
	size_t nAvg;         ///< Number of calculation time steps to average fluxes etc.
	size_t HzStep;       ///< Hazard step number (should be half of nStep in operational mode)
	bool   TsDump;       ///< Flag for time series dump
	bool   HzDump;       ///< Calculation of hazard information will be performed
	bool   PrDump;       ///< Flag for profile dump
	bool   XdataDump;    ///< Backup of Xdata will be performed
	bool   sdbDump;      ///< Dump to data base if required in operational mode
	bool   resFirstDump; ///< Flag to dump initial state of snowpack
};


class addSpecialKeyToCfg {
	public:
		addSpecialKeyToCfg();
		SnowpackConfig cfg;
};


class SnowpackInterface : public addSpecialKeyToCfg {

	public:
		SnowpackInterface();
		int init_sn();
		bool nextStep();
		
	public:
		mio::IOManager io;//
		SnowpackIO snowpackio;//
		CurrentMeteo Mdata;//
		SurfaceFluxes surfFluxes;//
		BoundCond sn_Bdata;//
		Slope slope;//
		Cumsum cumsum;//
		mio::Date current_date;//
		Meteo meteo;
		//
		//
		//
		mio::Timer meteoRead_timer;//
		mio::Timer run_timer;//
		time_t nowSRT;//
		MainControl mn_ctrl;//
		double i_time_zone ;//
		std::string variant; //
		std::string experiment; //
		std::string outpath; //
		bool useSoilLayers;//
		bool useCanopyModel; //
		double calculation_step_length;//
		double sn_dt; //
		int nSolutes;//
		double backup_days_between;//
		double first_backup;//
		bool label_snow;// J newly added
		bool allow_inflate;//
		bool prn_check;//
		bool grooming;//
		bool snow_write;//
		bool classify_profile;//
		bool profwrite;//
		double profstart;//
		double profdaysbetween;//
		bool tswrite;//
		double tsstart;//
		double tsdaysbetween;//
		bool precip_rates;//
		bool avgsum_time_series;//
		bool cumsum_mass;//
		double thresh_rain;//
		bool advective_heat;//
		bool soil_flux;//
		size_t i_stn=0;//
		double lw_in ;//
		double wind_scaling_factor;//
		double time_count_deltaHS;//
		ZwischenData sn_Zdata;//
		vector<SN_SNOWSOIL_DATA> vecSSdata;//
		vector<SnowStation> vecXdata;//
		SunObject sun;//  
		vector<ProcessDat> qr_Hdata;//
		vector<ProcessInd> qr_Hdata_ind;//
		double duration ;
		int counter;
		Hazard* haz;//
		Hazard hazard=*haz;//
		bool computed_one_timestep;//
		double meteo_step_length;//
		bool enforce_snow_height;//
};

void editMeteoData(mio::MeteoData& md, const string& variant, const double& thresh_rain);

bool validMeteoData(const mio::MeteoData& md, const string& StationName, const string& variant, const bool& enforce_snow_height, const bool& advective_heat, const bool& soil_flux, const unsigned int& nslopes);

void copyMeteoData(const mio::MeteoData& md, CurrentMeteo& Mdata,
                   const double& prevailing_wind_dir, const double& wind_scaling_factor, bool &iswr_is_net);
                                      
double getHS_last3hours(mio::IOManager &io, const mio::Date& current_date);

void setShortWave(CurrentMeteo& Mdata, const SnowStation& Xdata, const bool& iswr_is_net);

void dataForCurrentTimeStep(CurrentMeteo& Mdata, SurfaceFluxes& surfFluxes, vector<SnowStation>& vecXdata,
                            const Slope& slope, SnowpackConfig& cfg,
                            SunObject &sun,
                            double& precip, const double& lw_in, const double hs_a3hl6,
                            double& tot_mass_in,
                            const std::string& variant, const bool& iswr_is_net, Meteo &meteo);


void getOutputControl(MainControl& mn_ctrl, const mio::Date& step, const mio::Date& sno_step,
                      const double& calculation_step_length,
                      const double& tsstart, const double& tsdaysbetween,
                      const double& profstart, const double& profdaysbetween,
                      const double& first_backup, const double& backup_days_between);

bool readSlopeMeta(mio::IOManager& io, SnowpackIO& snowpackio, SnowpackConfig& cfg, const size_t& i_stn,
                   Slope& slope, mio::Date &current_date, vector<SN_SNOWSOIL_DATA> &vecSSdata,
                   vector<SnowStation> &vecXdata, ZwischenData &sn_Zdata, CurrentMeteo& Mdata,
                   double &wind_scaling_factor, double &time_count_deltaHS);

void addSpecialKeys(SnowpackConfig &cfg);

void writeForcing(Date d1, const Date& d2, const double& Tstep, IOManager &io);

void printStartInfo(const SnowpackConfig& cfg, const std::string& name);
}//namespace

#endif
