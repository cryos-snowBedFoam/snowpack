/*
fvScalarMatrix voidFractionEqn
(
      fvm::ddt(voidFraction)
  ==
	  miv/rho_ice
	+ mwv/rho_water
);
voidFractionEqn.relax();
voidFractionEqn.solve();

voidFraction = max(voidFraction, porosity_min);
voidFraction = min(voidFraction, porosity_max);
voidFraction.correctBoundaryConditions();
Info<< "voidFraction min/max = " << min(voidFraction).value() << ", " << max(voidFraction).value()	<< endl;
*/

voidFraction=1.-waterFraction-iceFraction;
voidFraction.correctBoundaryConditions();
Info<< "voidFraction min/max = " << min(voidFraction).value() << ", " << max(voidFraction).value()	<< endl;
