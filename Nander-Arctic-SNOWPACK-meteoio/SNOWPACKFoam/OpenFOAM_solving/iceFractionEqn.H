fvScalarMatrix iceFractionEqn
(
      fvm::ddt(iceFraction)
  ==
	- miv/rho_ice	  
);
iceFractionEqn.relax();
iceFractionEqn.solve();

/*
// right, multiple integration
forAll(iceFraction.internalField(), cellI)
{		
	iceFraction[cellI] = iceFraction.oldTime()[cellI]+runTime.deltaTValue()*miv[cellI]/rho_ice.value();
}*/

iceFraction = max(iceFraction, min_ice_content);
iceFraction = min(iceFraction, max_ice_content);
iceFraction.correctBoundaryConditions();
Info<< "iceFraction min/max = " << min(iceFraction).value() << ", " << max(iceFraction).value()	<< endl;

deltaThetaIce_accum-=miv*dimensionedScalar("dt", dimTime, runTime.deltaTValue())/rho_ice;
deltaThetaIce_accum.correctBoundaryConditions();

snowDen_accum-=rv*dimensionedScalar("dt", dimTime, runTime.deltaTValue());
snowDen_accum.correctBoundaryConditions();
