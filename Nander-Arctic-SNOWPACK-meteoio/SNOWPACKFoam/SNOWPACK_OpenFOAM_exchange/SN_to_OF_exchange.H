if(Xdata.getNumberOfNodes()-1>Xdata.SoilNode ) /// snow on the ground
{
	
	const size_t nN = Xdata.getNumberOfNodes();
	size_t nE = nN-1;

	const pointField& ctrs = mesh.cellCentres();

    Info << "------SN_to_OF_exchange---------------snowHeight_new_element_OF/snowHeight_old_element_OF:  " <<  gMax(ctrs).z() << ' ' << snowHeight_old_OF << endl;	

	/// creating a list for the snow/soil element height and related data from SN	
    std::vector<double> SN_el_height(nE,0.0);
    std::vector<double> SN_el_ID(nE,0.0);    
    		
	for(label k=0; k<nE; k++)   
	{
		SN_el_height[k]=0.5*(NDS[k].z+NDS[k+1].z);
		SN_el_ID[k]=scalar(k);				
	}

    /// To be sure the first element in OF which is snow always gets the interpolation values from snow elements from SN
	/// we insert the elements using the iterator "begin()+the index of elements we need to add before"
	if(nE>Xdata.SoilNode) /// snow on the ground
	{
		SN_el_height.insert(SN_el_height.begin(),NDS[0].z);
		SN_el_ID.insert(SN_el_ID.begin(),0.0);
		
		//
		SN_el_height.insert(SN_el_height.begin()+Xdata.SoilNode+1,NDS[Xdata.SoilNode].z); /// Xdata.SoilNode+1 because one element was added before
		SN_el_ID.insert(SN_el_ID.begin()+Xdata.SoilNode+1,scalar(Xdata.SoilNode));
		
		//
		SN_el_height.insert(SN_el_height.begin()+SN_el_height.size(),NDS[nE].z); /// this inserts an element after the end equal to SN_el_height.push_back(NDS[nE].z)
		SN_el_ID.insert(SN_el_ID.begin()+SN_el_ID.size(),scalar(nE-1));
	}

	/// fast algorithm	
	if(nE>Xdata.SoilNode ) /// snow on the ground
	{
		label NEz_OF=ctrs.size()/NEx_OF_procc;
		for(label k=0; k<NEz_OF; k++)   
		{
			//
			label cellI=k*NEx_OF_procc;
			//				
			scalar Id_scalar=solutio::LinearInterpolation(SN_el_height,SN_el_ID,ctrs[cellI].z()); //LogInterpolation // LinearInterpolation
			label el_lower=label(Id_scalar);
			label el_upper= (el_lower==nE-1) ? el_lower : el_lower+1;
			scalar w1=1.0-(Id_scalar-el_lower);
     		ElementData el_ave(EMS[el_lower],EMS[el_upper],w1);     		
			//
			scalar SN_to_OF_deltaTheta_ice=el_ave.SN_to_OF_deltaTheta_ice; 
			scalar SN_to_OF_deltaTheta_water=el_ave.SN_to_OF_deltaTheta_water;
			scalar vapTrans_deltaTheta_ice=el_ave.vapTrans_deltaTheta_ice; 
			scalar vapTrans_deltaTheta_water=el_ave.vapTrans_deltaTheta_water;
			scalar theta_ice=el_ave.theta[ICE];
			scalar theta_water=el_ave.theta[WATER]+el_ave.theta[WATER_PREF];
			scalar Te=el_ave.Te;
			scalar rg=el_ave.rg; /// grain radius in mm
			scalar ogs=el_ave.ogs; /// gain diamater, the optical grain size, in mm
			scalar Qmf=el_ave.Qmf; /// note this is already calculated in SN as [W/m3]
			scalar Qph=0.5*(el_ave.Qph_up+el_ave.Qph_down); ///note this is already calculated in SN as [W/m3]
			scalar sw_abs=el_ave.sw_abs; /// note this is [W/m2] in SNOWPACK...to have it per SN-element volume, we should devide it by el_ave.L
			//scalar dth_w=el_ave.dth_w;
			//scalar gradT=el_ave.gradT;
			scalar meltfreeze_tk=el_ave.meltfreeze_tk; /// in Kelvin
			//scalar apparent_rg=rg*Foam::pow(theta_water/theta_ice+1., 1./3.); /// the apparent rg for wet snow to calculate as_
			//scalar apparent_ogs=ogs*Foam::pow(theta_water/theta_ice+1., 1./3.); /// the apparent ogs for wet snow to calculate as_
			//scalar apparent_Theta=theta_water+theta_ice; /// the apparent content( water and ice) for wet snow to calculate as_
			scalar rho_snow=el_ave.Rho;
			//if(mag(Qph))
				    //Info << "------SN_to_OF_exchange----hahahahahahahahahaha Qph is not zero: " <<  Qph << endl;	

			//
			for(label i=0; i<NEx_OF_procc; i++)
			{
				label cI=cellI+i;
				//
				/*
				//test
				scalar var1=theta_ice-vapTrans_deltaTheta_ice+deltaThetaIce_accum[cI];
				scalar var2=iceFraction[cI]+SN_to_OF_deltaTheta_ice-vapTrans_deltaTheta_ice;
				//Info <<"++++++++++++++++++++++++++++++++||||||||||||||||||||++++++ " << var1 << ' ' << var2 << ' ' << mag(var1-var2) << endl;

				if (mag(var1-var2)>1e-3)
				{
					Info <<"FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF.................. " << var1 << ' ' << var2 << ' ' << mag(var1-var2) << endl;
				}
				*/
				//iceFraction[cI] +=SN_to_OF_deltaTheta_ice-vapTrans_deltaTheta_ice; // 
				//waterFraction[cI] +=SN_to_OF_deltaTheta_water-vapTrans_deltaTheta_water; // 
				//
				//
				//
				//
				//iceFraction[cI] =theta_ice-vapTrans_deltaTheta_ice+deltaThetaIce_accum[cI]; // this is wrong as vapTrans_deltaTheta_ice=theta_ice_aftervap-theta_ice_befVap...should be subtracted.....
				//waterFraction[cI] =theta_water-vapTrans_deltaTheta_water+deltaThetaWater_accum[cI];//theta_water;
				//voidFraction[cI] =1.-theta_ice-theta_water;
				///to avoid negative ice content due to het induced and large melting for low-content ice element...max(iceFraction, min_ice_content);
				iceFraction[cI] =max(theta_ice-vapTrans_deltaTheta_ice+deltaThetaIce_accum[cI], min_ice_content.value()); // this is wrong as vapTrans_deltaTheta_ice=theta_ice_aftervap-theta_ice_befVap...should be subtracted.....
				waterFraction[cI] =max(theta_water-vapTrans_deltaTheta_water+deltaThetaWater_accum[cI],min_water_content.value());//theta_water;
				voidFraction[cI] =1.-theta_ice-theta_water;
				//
				//
				//
				//
				//
				//
				///iceFraction[cI] =(1.-Indicator_[cI])*(iceFraction[cI]+SN_to_OF_deltaTheta_ice-vapTrans_deltaTheta_ice)
				///					  +Indicator_[cI]*(theta_ice-vapTrans_deltaTheta_ice+deltaThetaIce_accum[cI]); // this is wrong as vapTrans_deltaTheta_ice=theta_ice_aftervap-theta_ice_befVap...should be subtracted.....
				///waterFraction[cI] =(1.-Indicator_[cI])*(waterFraction[cI]+SN_to_OF_deltaTheta_water-vapTrans_deltaTheta_water)
				///					  +Indicator_[cI]*(theta_water-vapTrans_deltaTheta_water+deltaThetaWater_accum[cI]);//theta_water;
				///voidFraction[cI] =1.-theta_ice-theta_water;
				//
				///iceFraction[cI] =(1.-Indicator_[cI])*iceFraction[cI]+Indicator_[cI]*(theta_ice-vapTrans_deltaTheta_ice+deltaThetaIce_accum[cI]); // this is wrong as vapTrans_deltaTheta_ice=theta_ice_aftervap-theta_ice_befVap...should be subtracted.....
				///waterFraction[cI] =(1.-Indicator_[cI])*waterFraction[cI]+Indicator_[cI]*(theta_water-vapTrans_deltaTheta_water+deltaThetaWater_accum[cI]);//theta_water;
				///voidFraction[cI] =1.-theta_ice-theta_water;
				
				//
				el_ave.theta[ICE]=iceFraction[cI];
				el_ave.theta[WATER]=waterFraction[cI];
				el_ave.theta[WATER_PREF]=0.0;
				el_ave.theta[AIR]=voidFraction[cI];
				keff_ha[cI]=SnLaws::compSnowThermalConductivity_airEff(el_ave, 0., false);
				keff_i[cI]=SnLaws::compSnowThermalConductivity_iceEff(el_ave, 0., false);
				keff_w[cI]=SnLaws::compSnowThermalConductivity_waterEff(el_ave, 0., false);
				keffs[cI]=voidFraction[cI]*keff_ha[cI]+iceFraction[cI]*keff_i[cI]+waterFraction[cI]*keff_w[cI];
				//
				scalar apparent_rg=rg*Foam::pow(waterFraction[cI]/iceFraction[cI]+1., 1./3.); /// the apparent rg for wet snow to calculate as_
				scalar apparent_ogs=ogs*Foam::pow(waterFraction[cI]/iceFraction[cI]+1., 1./3.); /// the apparent ogs for wet snow to calculate as_
				scalar apparent_Theta=waterFraction[cI]+iceFraction[cI]; /// the apparent content( water and ice) for wet snow to calculate as_	
				//as_[cI] =6.0*apparent_Theta/(0.001*apparent_ogs); /// based on ogs
				//dp_[cI] =0.001*apparent_ogs; /// based on ogs
				as_[cI] =6.0*apparent_Theta/(0.001*2.*apparent_rg);  /// based on rg				
				dp_[cI] =0.001*2.*apparent_rg; /// based on rg
				ogs_[cI] =0.001*apparent_ogs; /// based on rg
				//K_[cI]=3.0*Foam::pow(0.5*apparent_ogs,2.)*Foam::exp(-0.013*rho_snow); // K_[cI]=3.0*Foam::pow(apparent_rg,2.)*Foam::exp(-0.013*rho_snow);
				//
				meltfreeze_tk_OF[cI] =meltfreeze_tk;
				q_swr[cI] =sw_abs/el_ave.L; ///q_swr is quite similar to q_swr3 but a bit different from q_swr2			
				///q_swr2[cI] =sw_abs*(initialCellSize_x*0.1)/mesh.V()[cI];		
				///q_swr3[cI] =(w1*EMS[el_lower].sw_abs/EMS[el_lower].L+(1.-w1)*EMS[el_upper].sw_abs/EMS[el_upper].L);		
				q_Qph[cI] =Qph;				
				q_Qmf[cI] =Qmf;				
				//
				if(mag(Qmf)>0.) /// check melting-refreezing
				{	
					q_swr[cI]=0.0; /// we should check it later
					Ti[cI] =Te; /// problematic: if we consider it, it means we should not consider shortwave absorption in OF, and this is fine just when we have melting-refreezing but not when we do not have melting refreezing...
								   /// one solution is to set Ti=0 and q_swr=0 for elements which have both water and ice together.
								   /// The second solution is not to set Ti=0 and q_swr=0 but limit the ice temperature to be always less than melting temperature...
					///Tg[cI] =Te; // not needed at all.
				}
				//
				if(ctrs[cI].z()>snowHeight_old_OF) /// new snow elements
				{	
					iceFraction[cI] =theta_ice;
					waterFraction[cI] =theta_water;
					voidFraction[cI] =1.-theta_ice-theta_water;
					//
					//
					//J addde: checking heterogenity 
					/*
					Indicator_[cI] =1.;
					forAll(xLocHet, i)
					{
						vector rCell=ctrs[cI];
						vector rHet(xLocHet[i],ctrs[cI].y(),NDS[Xdata.SoilNode].z);
						scalar rDistance=mag(rCell-rHet);
						//Info << "rCell/rHet/rDistance " << rCell << ' ' << rHet << ' ' << rDistance << endl;
						if( rDistance <= radius_het)
						{
							Indicator_[cI] =0.;
							iceFraction[cI] =0.5*theta_ice;
							waterFraction[cI] =0.5*theta_water;
							voidFraction[cI] =1.-theta_ice-theta_water;
							Info << " I am HEREEEEEEEEE " << endl;
						}	
					}
					*/	
					//
					//
					el_ave.theta[ICE]=iceFraction[cI];
					el_ave.theta[WATER]=waterFraction[cI];
					el_ave.theta[WATER_PREF]=0.0;
					el_ave.theta[AIR]=voidFraction[cI];
					keff_ha[cI]=SnLaws::compSnowThermalConductivity_airEff(el_ave, 0., false);
					keff_i[cI]=SnLaws::compSnowThermalConductivity_iceEff(el_ave, 0., false);
					keff_w[cI]=SnLaws::compSnowThermalConductivity_waterEff(el_ave, 0., false);
					keffs[cI]=voidFraction[cI]*keff_ha[cI]+iceFraction[cI]*keff_i[cI]+waterFraction[cI]*keff_w[cI];
					//
					scalar apparent_rg=rg*Foam::pow(waterFraction[cI]/iceFraction[cI]+1., 1./3.); /// the apparent rg for wet snow to calculate as_
					scalar apparent_ogs=ogs*Foam::pow(waterFraction[cI]/iceFraction[cI]+1., 1./3.); /// the apparent ogs for wet snow to calculate as_
					scalar apparent_Theta=waterFraction[cI]+iceFraction[cI]; /// the apparent content( water and ice) for wet snow to calculate as_	
					//as_[cI] =6.0*apparent_Theta/(0.001*apparent_ogs); /// based on ogs
					//dp_[cI] =0.001*apparent_ogs; /// based on ogs
					as_[cI] =6.0*apparent_Theta/(0.001*2.*apparent_rg);  /// based on rg				
					dp_[cI] =0.001*2.*apparent_rg; /// based on rg
					//K_[cI]=3.0*Foam::pow(0.5*apparent_ogs,2.)*Foam::exp(-0.013*rho_snow); // K_[cI]=3.0*Foam::pow(apparent_rg,2.)*Foam::exp(-0.013*rho_snow);					
					//
					Ti[cI] =Te;
					Tg[cI] =Te;
					rv[cI] =0.;
					miv[cI] =0.;
					mwv[cI] =0.;
					rv[cI] =0.;
					rho_vs[cI] =SnLaws::calc_rho_vs(Te); //rho_vs[cI] =coupler::Atmosphere::waterVaporDensity(Te, coupler::Atmosphere::vaporSaturationPressure(Te));
					rho_v[cI] =rho_vs[cI];				
					rho_a[cI] =p[cI]*Mair.value()/gaz_constant.value()/Tg[cI]; //P_ref.value()*Mair.value()/gaz_constant.value()/Tg[cI];
					rho[cI] =rho_v[cI]+rho_a[cI];
					scalar pv=rho_v[cI]/(Mvap.value()/gaz_constant.value()/Tg[cI]);
					///p[cI]=P_ref.value()+pv; //com
					///p_rgh[cI] = p[cI] - rho[cI]*gh[cI]; //com 	
					///U[cI] = vector(0,0,0);
					deltaThetaIce_accum[cI] = 0.; 
					deltaThetaWater_accum[cI] = 0.;
					//
					snowDen_accum[cI] = 0.; 					
				}
				//
				/*
				if(k==NEz_OF-1 && ctrs[cI].z()<snowHeight_old_OF)
				{
					iceFraction[cI] =theta_ice;
					waterFraction[cI] =theta_water;
					voidFraction[cI] =1.-theta_ice-theta_water;
				}*/	
			}			
		}
		//
		//phi=linearInterpolate(rho*U) & mesh.Sf();
		//phi.storeOldTime();						 
		//
		Tg.boundaryFieldRef()[patchIdTop]==NDS[Xdata.getNumberOfNodes()-1].T;		
		Tg.boundaryFieldRef()[patchIdBottom]==NDS[Xdata.SoilNode].T;
		Tg.correctBoundaryConditions();		
		//
		Ti.boundaryFieldRef()[patchIdTop]==NDS[Xdata.getNumberOfNodes()-1].T;		
		Ti.boundaryFieldRef()[patchIdBottom]==NDS[Xdata.SoilNode].T;
		Ti.correctBoundaryConditions();				
		//
		scalar rhovs_top=SnLaws::calc_rho_vs(NDS[Xdata.getNumberOfNodes()-1].T);
		rho_v.boundaryFieldRef()[patchIdTop]==rhovs_top;		
		rho_v.correctBoundaryConditions();				
		//
		iceFraction.correctBoundaryConditions();
		waterFraction.correctBoundaryConditions();
		voidFraction.correctBoundaryConditions();
		q_swr.correctBoundaryConditions();
		q_Qph.correctBoundaryConditions();
		//K_.correctBoundaryConditions();
		as_.correctBoundaryConditions();
		dp_.correctBoundaryConditions();
		ogs_.correctBoundaryConditions();
		meltfreeze_tk_OF.correctBoundaryConditions();
		rho_vs.correctBoundaryConditions();
		rho_v.correctBoundaryConditions();
		rho_a.correctBoundaryConditions();
		rho.correctBoundaryConditions();
		///p.correctBoundaryConditions(); //com 
		//p_rgh.correctBoundaryConditions();
		U.correctBoundaryConditions(); //com 
		deltaThetaIce_accum.correctBoundaryConditions(); //com 
		deltaThetaWater_accum.correctBoundaryConditions(); //com 
		//
		snowDen_accum.correctBoundaryConditions();
		//
		keff_ha.correctBoundaryConditions();
		keff_i.correctBoundaryConditions();
		keff_w.correctBoundaryConditions();
		keffs.correctBoundaryConditions();
		//
		Indicator_.correctBoundaryConditions(); // Jafari added for Het
		Indicator_.storeOldTime(); // Jafari added for Het		
		//
		p_rgh = p - rho*gh;	
		Cp_g=(Cp_v_ref*rho_v+Cp_a_ref*rho_a)/rho;
		wg=voidFraction*rho*Cp_g/(voidFraction*rho*Cp_g+iceFraction*rho_ice*Cp_i_ref+waterFraction*rho_water*Cp_w_ref);
		wi=(iceFraction*rho_ice*Cp_i_ref+waterFraction*rho_water*Cp_w_ref)/(voidFraction*rho*Cp_g+iceFraction*rho_ice*Cp_i_ref+waterFraction*rho_water*Cp_w_ref);            
		Mg=1.0/(rho_a/rho/Mair+rho_v/rho/Mvap);
		//
		/// if we solve any fields as PDEs in OpenFOAM such as rho,iceFraction,waterFraction,Tg,Ti,U, p_rgh, rho_v,...
		/// and if these field are updated here from SNOWPACK, we need to set their old values the same as the updated values done here.....
		/// To do then, we use fieldName.storeOldTime() which updates old values based on new ones or iceFraction.oldTime()=iceFraction
		Ti.storeOldTime();
		Tg.storeOldTime();
		iceFraction.storeOldTime();
		waterFraction.storeOldTime();
		voidFraction.storeOldTime();
		rho.storeOldTime();
		U.storeOldTime(); //com 
		rho_v.storeOldTime();
		p_rgh.storeOldTime(); //com 		
		p.storeOldTime(); //com 		
	}
}


