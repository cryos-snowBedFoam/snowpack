Cp_g=(Cp_v_ref*rho_v+Cp_a_ref*rho_a)/rho;
wg=voidFraction*rho*Cp_g/(voidFraction*rho*Cp_g+iceFraction*rho_ice*Cp_i_ref+waterFraction*rho_water*Cp_w_ref);
wi=(iceFraction*rho_ice*Cp_i_ref+waterFraction*rho_water*Cp_w_ref)/(voidFraction*rho*Cp_g+iceFraction*rho_ice*Cp_i_ref+waterFraction*rho_water*Cp_w_ref);
/*
//for the dry snow, the original Hansaen's model
keff_ha=((1-voidFraction)+1.0/((1-voidFraction)/ki*(ka+Liv*Dv_a*activeMassChange*drho_vs_dT)+voidFraction))*ka;
keff_i=(1-voidFraction)*ki;
*/

/*
//for the wet snow, extended Hansen's model
keff_ha=(iceFraction+waterFraction)*ka
				  +ka/( voidFraction
				        +iceFraction*(ka+Liv*Dv_a*drho_vs_dT)/ki
				        +waterFraction*(ka+Liv*Dv_a*drho_vs_dT)/kw
				      );			      
keff_i=(iceFraction+waterFraction)*ki;
keff_w=(iceFraction+waterFraction)*kw;

keffs=voidFraction*keff_ha+iceFraction*keff_i+waterFraction*keff_w;
*/


//
surfaceScalarField cpPhi("cpPhi", fvc::interpolate(Cp_g)*phi);
surfaceVectorField sumTgCpiJi("sumTgCpiJi", fvc::interpolate(Tg*(Cp_v_ref-Cp_a_ref)*Jv));
fvScalarMatrix TgEqn
(
	fvm::ddt(voidFraction*rho*Cp_g,Tg)
   +fvm::div(cpPhi,Tg)	
  ==
	fvm::laplacian(voidFraction*keff_ha, Tg)
   +Porosityd_Pdt //based on revised formulations 
   +(U & fvc::grad(p)) //based on revised formulations    
   -fvc::div(Tg*(Cp_v_ref-Cp_a_ref)*Jv) // or -fvc::div(sumTgCpiJi), based on revised formulations    	
   +( 
		int(runTime.value())%int(couplingInterval)==0
		?	
		 fvm::Sp(hcas*(wg-1),Tg)+hcas*wi*Ti//fvm::SuSp(hcas*((wg-1)*Tg+wi*Ti)/Tg,Tg)//fvm::Sp(hcas*((wg-1)*Tg+wi*Ti)/Tg,Tg)//fvm::Sp(hcas*(wg-1),Tg)+hcas*wi*Ti//hcas*((wg-1)*Tg+wi*Ti)
		+fvm::Sp(rv*Cp_v_ref,Tg)//+fvm::Sp(rv*Cp_v_ref,Tg)
		:
		 fvm::Sp(0.*hcas*(wg-1),Tg)+0.*hcas*wi*Ti
		+fvm::Sp(0.*rv*Cp_v_ref,Tg)
	)	   
);
TgEqn.relax();
TgEqn.solve();
Info<< "Tg min/max = " << min(Tg).value() << ", " << max(Tg).value()	<< endl;


/// old correct form
/*
fvScalarMatrix TiEqn
(
    fvm::ddt(iceFraction*rho_ice*Cp_i_ref,Ti)
  ==
	fvm::laplacian(iceFraction*keff_i, Ti)
   +( 
		int(runTime.value())%int(couplingInterval)==0
		?
		-fvm::Sp(hcas*wi,Ti)-hcas*(wg-1)*Tg //-fvm::Sp(hcas*((wg-1)*Tg+wi*Ti)/Ti,Ti)//-fvm::Sp(hcas*wi,Ti)-hcas*(wg-1)*Tg //-hcas*((wg-1)*Tg+wi*Ti)
		-fvm::Sp(rv*Cp_v_ref*wi,Ti)-rv*Cp_v_ref*wg*Tg//-fvm::Sp(rv*Cp_v_ref*(wg*Tg+wi*Ti)/Ti,Ti)//-fvm::Sp(rv*Cp_v_ref*wi,Ti)-rv*Cp_v_ref*wg*Tg 
		-rv*(Cp_i_ref-Cp_v_ref)*T_ref//-fvm::Sp(rv*(Cp_i_ref-Cp_v_ref)*T_ref/Ti,Ti)//-rv*(Cp_i_ref-Cp_v_ref)*T_ref
		-rv*Liv
		:
		-fvm::Sp(0.*hcas*wi,Ti)-0.*hcas*(wg-1)*Tg 
		-fvm::Sp(0.*rv*Cp_v_ref*wi,Ti)-0.*rv*Cp_v_ref*wg*Tg 
		-0.*rv*(Cp_i_ref-Cp_v_ref)*T_ref
		-0.*rv*Liv
	)    
);
TiEqn.relax();
TiEqn.solve();
Info<< "Ti min/max = " << min(Ti).value() << ", " << max(Ti).value()	<< endl;
*/

// old correct form with miw 
fvScalarMatrix TiEqn
(
    fvm::ddt(iceFraction*rho_ice*Cp_i_ref+waterFraction*rho_water*Cp_w_ref,Ti)
  ==
	fvm::laplacian(iceFraction*keff_i+waterFraction*keff_w, Ti)
   +( 
		int(runTime.value())%int(couplingInterval)==0
		?
		-fvm::Sp(hcas*wi,Ti)-hcas*(wg-1)*Tg
		 ///ice-vapor interaction		 
		-fvm::Sp(miv*Cp_v_ref*wi,Ti)-miv*Cp_v_ref*wg*Tg 
		-miv*(Cp_i_ref-Cp_v_ref)*T_ref
		-miv*Liv
		 ///water-vapor interaction		
		-fvm::Sp(mwv*Cp_v_ref*wi,Ti)-mwv*Cp_v_ref*wg*Tg 		
		-mwv*(Cp_w_ref-Cp_v_ref)*T_ref
		-mwv*Lwv
		 ///shortwave radiation source term		
		+fvm::Sp(q_swr/Ti,Ti) // It is a bit tricky as q_swr is positive and make the left matix less dominance...the best practice is to make use of fvm:SpSu //+q_swr		
		 ///ice-water melting source term based on miw:
		 ///note that in SNOWPACK, the following term is not used as there the melting-refreezing is treated explicitly
		 ///and we do not need these terms here. However, for the sea-ice version we have the phase change source term as q_Qph		
		 //+miw*(Cp_w_ref-Cp_i_ref)*T_ref-miw*Liw
		 ///ice-water melting source term just for SNOWPACK-sea ice version	
		//+fvm::Sp(q_Qph/Ti,Ti) //+q_Qph
		:
		-fvm::Sp(0.*hcas*wi,Ti)-0.*hcas*(wg-1)*Tg
		 ///ice-vapor interaction		 
		-fvm::Sp(0.*miv*Cp_v_ref*wi,Ti)-0.*miv*Cp_v_ref*wg*Tg 		
		-0.*miv*(Cp_i_ref-Cp_v_ref)*T_ref
		-0.*miv*Liv
		 ///water-vapor interaction	
		-fvm::Sp(0.*mwv*Cp_v_ref*wi,Ti)-0.*mwv*Cp_v_ref*wg*Tg		
		-0.*mwv*(Cp_w_ref-Cp_v_ref)*T_ref
		-0.*mwv*Lwv
		 //shortwave radiation source term		
		+0.*q_swr
		 ///ice-water melting source term based on miw:
		 ///note that in SNOWPACK, the following term is not used as there the melting-refreezing is treated explicitly
		 ///and we do not need these terms here. However, for the sea-ice version we have the phase change source term as q_Qph		
		//+0.*miw*(Cp_w_ref-Cp_i_ref)*T_ref-0.*miw*Liw 
		 ///ice-water melting source term just for SNOWPACK-sea ice version	
		//+0.*q_Qph 
	)    
);
TiEqn.relax();
TiEqn.solve();
forAll(mesh.cellCentres(), cellI)
{
	if(waterFraction[cellI]>0. && mag(q_Qmf[cellI])>0.)
	{
		Ti[cellI]=meltfreeze_tk_OF[cellI];
	}
}
Ti = min(Ti, meltfreeze_tk_OF); // to limit the ice temperature
Info<< "Ti min/max = " << min(Ti).value() << ", " << max(Ti).value()	<< endl;

/*
// new form without miw 
fvScalarMatrix TiEqn
(
    (iceFraction*rho_ice*Cp_i_ref+waterFraction*rho_water*Cp_w_ref)*fvm::ddt(Ti)
  ==
	fvm::laplacian(iceFraction*keff_i+waterFraction*keff_w, Ti)
   +( 
		int(runTime.value())%int(couplingInterval)==0
		?
		-fvm::Sp(hcas*wi,Ti)-hcas*(wg-1)*Tg 
		 //ice-vapor interaction
		-fvm::Sp(miv*(Cp_v_ref*wi-Cp_i_ref),Ti)-miv*Cp_v_ref*wg*Tg
		-miv*(Cp_i_ref-Cp_v_ref)*T_ref
		-miv*Liv
		 //ice-vapor interaction		
		-fvm::Sp(mwv*(Cp_v_ref*wi-Cp_w_ref),Ti)-mwv*Cp_v_ref*wg*Tg
		-mwv*(Cp_w_ref-Cp_v_ref)*T_ref
		-mwv*Lwv
		 //shortwave radiation source term		
		+q_swr
		 //ice-water melting source term just for the sea-ice case		
		+q_Qph
		:
		-fvm::Sp(0.*hcas*wi,Ti)-0.*hcas*(wg-1)*Tg 
		 //ice-vapor interaction
		-fvm::Sp(0.*miv*(Cp_v_ref*wi-Cp_i_ref),Ti)-0.*miv*Cp_v_ref*wg*Tg
		-0.*miv*(Cp_i_ref-Cp_v_ref)*T_ref
		-0.*miv*Liv
		 //ice-vapor interaction		
		-fvm::Sp(0.*mwv*(Cp_v_ref*wi-Cp_w_ref),Ti)-mwv*Cp_v_ref*wg*Tg
		-0.*mwv*(Cp_w_ref-Cp_v_ref)*T_ref
		-0.*mwv*Lwv
		 //shortwave radiation source term		
		+0.*q_swr
		 //ice-water melting source term just for the sea-ice case		
		+0.*q_Qph		
	)    
);
TiEqn.relax();
TiEqn.solve();
Info<< "Ti min/max = " << min(Ti).value() << ", " << max(Ti).value()	<< endl;
*/

//Ti = min(Ti, meltfreeze_tk_OF); // to limit the ice temperature

#include "updateEquationOfState.H"//"updateEquationOfState.H"//"updateHeatAndMassTransferCoeffs.H"
