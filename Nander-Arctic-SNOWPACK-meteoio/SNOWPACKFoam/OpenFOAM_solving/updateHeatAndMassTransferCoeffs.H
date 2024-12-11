{
	const pointField& ctrs = mesh.cellCentres();
	
	#include "updateEquationOfState.H" // J added

	Info<< "The saturated water vapor densiy is calculated based temperature \n";

	Tm=wg*Tg+wi*Ti;

	forAll(ctrs, cellI)
	{
		rho_vs[cellI] = SnLaws::calc_rho_vs(Tm[cellI]);
	}	
	rho_vs.correctBoundaryConditions();

	Info<< "The drho_v_dT is calculated based on temperature \n";
	forAll(ctrs, cellI)
	{
		drho_vs_dT[cellI] = SnLaws::calc_drho_vs_dT(Tm[cellI]);
	}	
	drho_vs_dT.correctBoundaryConditions();
		
	Info<< "Calculating the rate of mass phase change between ice and vapor \n";
	forAll(ctrs, cellI)
	{
		scalar hm = rho_ice.value()/rho_vs[cellI]/9.7e9;
		scalar as = as_[cellI];
		hmas[cellI] = hm*as;
		rv[cellI] = hm*as*(rho_vs[cellI]-rho_v[cellI]);
		if(min_ice_content.value()>=iceFraction[cellI]+runTime.deltaTValue()*-rv[cellI]/rho_ice.value())
		{
			Info<< "*****WARNING: iceContent<= min_ice_content:  \n";
			Info<< "*****: iceContent= " << iceFraction[cellI] << ", min_ice_content= " << min_ice_content.value() << endl;
			rv[cellI] = (iceFraction[cellI]-min_ice_content.value())/(runTime.deltaTValue()/rho_ice.value());
			hmas[cellI] = (rv[cellI]==0) ? 0.0 : rv[cellI]/(rho_vs[cellI]-rho_v[cellI]);
		}
		if(max_ice_content.value()<=iceFraction[cellI]+runTime.deltaTValue()*-rv[cellI]/rho_ice.value())
		{
			Info<< "*****WARNING: max_ice_content<=iceContent:  \n";
			Info<< "*****: iceContent= " << iceFraction[cellI] << ", max_ice_content= " << max_ice_content.value() << endl;
			rv[cellI] = (iceFraction[cellI]-max_ice_content.value())/(runTime.deltaTValue()/rho_ice.value());
			hmas[cellI] = (rv[cellI]==0) ? 0.0 : rv[cellI]/(rho_vs[cellI]-rho_v[cellI]);
		}
		//Info << "rv/miv/mwv " << rv[cellI] << ' ' << miv[cellI] << ' ' << mwv[cellI] << endl;
	}
	rv=activeMassChange*rv;
	hmas=activeMassChange*hmas;
	rv.correctBoundaryConditions();
	hmas.correctBoundaryConditions();

	Info<< "Calculating the hc*as \n";
	forAll(ctrs, cellI)
	{
		scalar hm = rho_ice.value()/rho_vs[cellI]/9.7e9;
		//scalar Sh = hm*dp.value()/Dv_a.value();
		scalar Sh = hm*dp_[cellI]/Dv_a.value();
		scalar Nu = Sh;
		
		//scalar hc = Nu*ka.value()/dp.value();
		scalar hc = Nu*ka.value()/dp_[cellI];
		scalar as = as_[cellI];
		hcas[cellI] = hc*as;		
	}
	hcas.correctBoundaryConditions();
}
