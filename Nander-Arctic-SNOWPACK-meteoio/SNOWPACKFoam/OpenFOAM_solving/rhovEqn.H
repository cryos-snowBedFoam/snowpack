{
	const pointField& ctrs = mesh.cellCentres();
	
	Deff=(1-voidFraction)*voidFraction*Dv_a+voidFraction*Dv_a/((1-voidFraction)/ki*(ka+Liv*Dv_a*activeMassChange*drho_vs_dT)+voidFraction);

	surfaceScalarField UAf("UAf", fvc::interpolate(1.0/rho)*phi);	

	fvScalarMatrix rhovEqn
	(
		  fvm::ddt(voidFraction,rho_v)
		 +fvm::div(UAf,rho_v)
	  ==
		  fvm::laplacian(Deff,rho_v)
		 -fvm::Sp(hmas,rho_v)+hmas*rho_vs//-0.5*(fvm::Sp(hmas,rho_v)+hmas*rho_v)+hmas*rho_vs//-fvm::Sp(hmas,rho_v)+hmas*rho_vs//+rv
	);
	rhovEqn.relax();
	rhovEqn.solve();

	/////****/////rho_v=0.0*rho_v; //for validation purpose
	rho_a=rho-rho_v;
	Mg=1.0/(rho_a/rho/Mair+rho_v/rho/Mvap); // J added

	Jv=-Deff*fvc::grad(rho_v); /// needed based on revised formulation for gas phase heat transfer

	Info<< "****from continuity, rho min/max = " << min(rho).value() << ", " << max(rho).value()	<< endl;
	Info<< "****from continuity, rho_v min/max = " << min(rho_v).value() << ", " << max(rho_v).value()	<< endl;

	rv = activeMassChange*hmas*(rho_vs-rho_v);
	rv.correctBoundaryConditions();

	//
	forAll(ctrs, cellI)
	{
		scalar waterMass=waterFraction[cellI]*rho_water.value()*mesh.V()[cellI];
		scalar waterMassPerTimePerVolume=waterMass/(mesh.V()[cellI]*runTime.deltaTValue());
		//
		if(rv[cellI]<=0. && Tm[cellI]<meltfreeze_tk_OF[cellI]) /// add ice
		{
			mwv[cellI]=0.0;
			miv[cellI]=rv[cellI];
		}
		if(rv[cellI]<=0. && Tm[cellI]>=meltfreeze_tk_OF[cellI]) /// add water
		{
			mwv[cellI]=0.0;
			miv[cellI]=rv[cellI];
		}
		if(rv[cellI]>=0.) /// if water is present first evaporation then the lefover is used for sublimation
		{
			mwv[cellI]=(rv[cellI]>=waterMassPerTimePerVolume) ? waterMassPerTimePerVolume : rv[cellI];
			miv[cellI]=rv[cellI]-mwv[cellI];
		}
	}
	mwv=activeMassChange*mwv;
	mwv.correctBoundaryConditions();
	miv=activeMassChange*miv;	
	miv.correctBoundaryConditions();
}		
		
