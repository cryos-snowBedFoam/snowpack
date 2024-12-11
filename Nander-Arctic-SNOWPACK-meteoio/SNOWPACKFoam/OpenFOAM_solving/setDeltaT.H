/*---------------------------------------------------------------------------*\
  =========                 |
  \\      /  F ield         | OpenFOAM: The Open Source CFD Toolbox
   \\    /   O peration     |
    \\  /    A nd           | Copyright (C) 2011 OpenFOAM Foundation
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

Global
    setDeltaT

Description
    Reset the timestep to maintain a constant maximum courant Number.
    Reduction of time-step is immediate, but increase is damped to avoid
    unstable oscillations.

\*---------------------------------------------------------------------------*/

//sumDTime+=runTime.deltaTValue();
//if (sumDTime>limit){
	//sumDTime=0.;
	//maxDeltaT2=min(maxDeltaT2+100, maxDeltaT);
	//limit=40*maxDeltaT2;
//}
	
if (adjustTimeStep)
{
    scalar maxDeltaTFact = maxCo/(CoNum + SMALL);
    scalar deltaTFact = min(min(maxDeltaTFact, 1.0 + 0.1*maxDeltaTFact), 1.2);

    runTime.setDeltaT
    (
        min
        (
            deltaTFact*runTime.deltaTValue(),
            maxDeltaT
        )
    );  

    Info<< "adjustTimeStep deltaT modified = " <<  runTime.deltaTValue() << endl;
}
else
{
	runTime.setDeltaT(maxDeltaT);
    Info<< "no adjustTimeStep deltaT modified = " <<  runTime.deltaTValue() << endl;	
}

/*
if (CoNum<=1.0 && runTime.outputTime())  /// CoNum is calculated max courant number
{
	runTime.setDeltaT(SN_deltaT);
	Info<< "-----CoNum<=1.0 so deltaT modified = " <<  runTime.deltaTValue() << endl;
}
*/	

if(cum_OF_deltaT+runTime.deltaTValue()>=SN_deltaT)
{
	runTime.setDeltaT(SN_deltaT-cum_OF_deltaT);
	if ((SN_deltaT-cum_OF_deltaT)<0.5)
	{
		runTime.setDeltaT(0.5);
	}
	Info<< "deltaT modified to complete one SNOWPACK time step= " <<  runTime.deltaTValue() << endl;
	doNextStep_OF=false;
}
else
{
	cum_OF_deltaT+=runTime.deltaTValue();
}	

// ************************************************************************* //
