fvScalarMatrix waterFractionEqn
(
      fvm::ddt(waterFraction)
  ==
	- mwv/rho_water
);
waterFractionEqn.relax();
waterFractionEqn.solve();

waterFraction = max(waterFraction, min_water_content);
waterFraction = min(waterFraction, max_water_content);
waterFraction.correctBoundaryConditions();
Info<< "waterFraction min/max = " << min(waterFraction).value() << ", " << max(waterFraction).value()	<< endl;

deltaThetaWater_accum-=mwv*dimensionedScalar("dt", dimTime, runTime.deltaTValue())/rho_water;
deltaThetaWater_accum.correctBoundaryConditions();
