const pointField& ctrs = mesh.cellCentres(); // J just added	
label NEz_OF=ctrs.size()/NEx_OF_procc;

	doNextStep_OF=true;
	cum_OF_deltaT=0.;
	while (doNextStep_OF)
    {
		
        #include "readTimeControls.H"
        #include "compressibleCourantNo.H"
        #include "setDeltaT.H"

        runTime++;

        Info<< "OpenFOAM Time = " << runTime.timeName() << " corresponding to SNOWPACK date = " << coupling.current_date.toString(mio::Date::DIN).c_str() << nl << endl;

		//if(Xdata.getNumberOfNodes()-1>Xdata.SoilNode ) /// snow on the ground    
		//if(Xdata.getNumberOfNodes()-1>Xdata.SoilNode && NEz_OF>=2 ) /// snow on the ground    ctrs.size()/NEx_OF_procc
		bool byPass_condition=(cum_OF_deltaT>byPass_threshold ? (runTime.deltaTValue()<5.0 ?  true : false) : false);
		if(Xdata.getNumberOfNodes()-1>Xdata.SoilNode && NEz_OF>=2  && !byPass_condition) /// snow on the ground  and no condition fo by-pass
		{///***
        #include "iceFractionEqn.H"
        #include "waterFractionEqn.H"
        #include "voidFractionEqn.H"        
		#include "updateHeatAndMassTransferCoeffs.H"
        #include "rhoEqn.H"
        
        // --- Pressure-velocity PIMPLE corrector loop
        while (pimple.loop())
        {
            #include "UEqn.H"
            //#include "rhoEqn.H"
            #include "rhovEqn.H"
            #include "EEqn.H"

            // --- Pressure corrector loop
            while (pimple.correct())
            {
                #include "pEqn.H"
            }

			// J commented
            //if (pimple.turbCorr())
            //{
                //turbulence->correct();
            //}
            // J commented
        }

        //new
		#include "updateEquationOfState.H"          

        //rho = thermo.rho(); // J commented
        
 		//#include "RaNumberCalculation.H"       
        }///***

		/// note that when we byPass_condition is satisfied we need to freez the flow calculation but still need to updae volume fractions
		if(Xdata.getNumberOfNodes()-1>Xdata.SoilNode && NEz_OF>=2  && byPass_condition) /// snow on the ground  and no condition fo by-pass        
        {
			#include "iceFractionEqn.H"
			#include "waterFractionEqn.H"
			#include "voidFractionEqn.H"        	
		}
			
        runTime.write();
        
        // archived procedures      
		#include "runArchiving.H" 
        
        Info<< "ExecutionTime = " << runTime.elapsedCpuTime() << " s"
            << "  ClockTime = " << runTime.elapsedClockTime() << " s"
            << nl << endl;
    }

