/*---------------------------------------------------------------------------*\
  =========                 |
  \\      /  F ield         | OpenFOAM: The Open Source CFD Toolbox
   \\    /   O peration     |
    \\  /    A nd           | Copyright (C) 2011-2016 OpenFOAM Foundation
     \\/     M anipulation  |
-------------------------------------------------------------------------------
License
    This file is part of OpenFOAM.

    OpenFOAM is free software: you can redistribute it and/or modify it
    under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenFOAM is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
    for more details.

    You should have received a copy of the GNU General Public License
    along with OpenFOAM.  If not, see <http://www.gnu.org/licenses/>.

Application
    buoyantPimpleFoam

Description
    Transient solver for buoyant, turbulent flow of compressible fluids for
    ventilation and heat-transfer.

    Turbulence is modelled using a run-time selectable compressible RAS or
    LES model.

\*---------------------------------------------------------------------------*/
/// standard c++ libraries should be first...
#include <cstdlib>
#include <string>
#include <vector>
#include <algorithm>
#include <cmath>
#include <sstream> 
#include <iostream> 
#include <iomanip> 
#include <fstream> 
#include <map> 
#include <set> 
#include <numeric>

/// SNOWPACK interface library should be placed before OpenFOAM ones
#include "interface_SNOWPACK.H" /// the namespace of this library is "coupler". 
//using namespace std;
//using namespace mio;
//using namespace coupler;

/// linear-log std::vector interpolation
#include "DataInterpolation.hpp" /// the namespace of this library is "solutio". 

///  GNU Scientific Library
//#include <gsl/gsl_math.h>
//#include <gsl/gsl_spline.h>
//#include <gsl/gsl_errno.h>
//#include <gsl/gsl_interp.h>

/// OpenFOAM Libraries should be the last ones to avoid namespace conflicts
#include "fvCFD.H"
#include "dynamicFvMesh.H"
//#include "rhoThermo.H" // J commented
//#include "turbulentFluidThermoModel.H" // J commented
//#include "radiationModel.H" // J commented
#include "fvOptions.H"
#include "pimpleControl.H"
#include "Random.H" // J addded

#include "IFstream.H"
#include "OFstream.H"
#include "IStringStream.H"//Jafari added
#include "OStringStream.H"//Jafari added
#include "IOmanip.H"//Jafari added
#include "DLList.H"//Jafari added
#include "interpolation.H"//Jafari added

#include "argList.H"

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * //
///for synchronizing processors if needed
inline void barrierMPI()
{
	//blocking
	if(Pstream::parRun())
	{
		label tmp = Pstream::myProcNo();
		reduce(tmp, sumOp<label>());
	}
	//blocking				
}

int main(int argc, char *argv[])
{
    #include "postProcess.H"

    #include "setRootCase.H"
    #include "createTime.H"    
    #include "createDynamicFvMesh.H"
    #include "createControl.H"
    #include "createFields.H"
  //#include "createFieldRefs.H" // J commented
    #include "createFvOptions.H"
    #include "initContinuityErrs.H"
    #include "createTimeControls.H"
    #include "compressibleCourantNo.H"
    #include "setInitialDeltaT.H"
    
    #include "createSNOWPACK.H" /// any mesh update and move should be done after previous createName.H
    
    //turbulence->validate(); // J commented
    
    Info<< "\nStarting time loop for SNOWPACK-OpenFOAM coupling\n" << endl;		
	do
	{
		doNextStep_SN=coupling.nextStep();barrierMPI();
		///runTime++; // should be deleted
		///Info<< "OpenFOAM Time = " << runTime.timeName() << " corresponding to SNOWPACK date = " << coupling.current_date.toString(mio::Date::DIN).c_str() << nl << endl;// should be deleted
		#include "SN_to_OF_dynMesh.H"
 		#include "SN_to_OF_exchange.H"
 		#include "OF_solving.H"
 		runTime.write();
  		#include "OF_to_SN_exchange.H"
 	}while(doNextStep_SN);	
	barrierMPI();

    Info<< "End for SNOWPACK-OpenFOAM coupling\n" << endl;

    return 0;
}


// ************************************************************************* //
