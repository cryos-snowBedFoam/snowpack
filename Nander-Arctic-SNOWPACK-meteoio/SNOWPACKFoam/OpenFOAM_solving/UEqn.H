//volScalarField rho_snow("rho_snow", iceFraction*rho_ice+waterFraction*rho_water);
//forAll(mesh.cellCentres(), cellI)
forAll(K_.internalField(), cellI)
{
	/// Calonne model
	K_[cellI]=3.0*Foam::pow(dp_[cellI]/2.0,2.)*Foam::exp(-0.013*(iceFraction[cellI]*rho_ice.value()+waterFraction[cellI]*rho_water.value()));
	
	/// Carman-Kozeny (CK) model
	//K_[cellI]=4.0*Foam::pow(dp_[cellI]/2.0,2.)*Foam::pow(voidFraction[cellI],3.)
	//		 /(180.*Foam::pow(1.-voidFraction[cellI],2.));
	//Info << "1dp_/rho_snow/K_ " << dp_[cellI] << ' ' << rho_snow[cellI] << ' ' << K_[cellI] << endl;	
}
//K_=dimensionedScalar("permeability", K_.dimensions(), 1.0)*3.0*Foam::pow(dp_[0]/2.0,2.)*Foam::exp(-0.013*(1-voidFraction)*rho_ice.value());
K_.correctBoundaryConditions();

volScalarField cF("cF", 1.75*Foam::pow(150.0,-0.5)*Foam::pow(voidFraction,-1.5));

MRF.correctBoundaryVelocity(U);

fvVectorMatrix UEqn
(
//    fvm::ddt(rho, U) + fvm::div(phi, U) +// J commented
//  + MRF.DDt(rho, U) // J commented
//  + turbulence->divDevRhoReff(U) // J commented
   //fvm::ddt(rho, U)+
   fvm::Sp(mu_a_ref/K_,U)
  +fvm::Sp(rho*cF*mag(U)/Foam::pow(K_,0.5),U)
 ==
	fvOptions(rho, U)	
);

UEqn.relax();

fvOptions.constrain(UEqn);

if (pimple.momentumPredictor())
{
	solve
	(
		UEqn
	 ==
		fvc::reconstruct
		(
			(
			  - ghf*fvc::snGrad(rho)
			  - fvc::snGrad(p_rgh)
			)*mesh.magSf()
		)
	);

	fvOptions.correct(U);
	//K = 0.5*magSqr(U); // J commented
}

Info<< "U min/max = " << min(U).value() << ", " << max(U).value()	<< endl;
