/////////////////////////////////////////////////////////////////////
    Info<< "Reading transportProperties\n" << endl;
    IOdictionary transportProperties
    (
        IOobject
        (
            "transportProperties",
            runTime.constant(),
            mesh,
            IOobject::MUST_READ_IF_MODIFIED,
            IOobject::NO_WRITE
        )
    );

    Info<< "\n";
    Info<< "Reading reference temperature Tref from SNOWPACK";
    dimensionedScalar T_ref("T_ref", dimTemperature, Constants::meltfreeze_tk);
	Info<< " : " << T_ref.value() << "\n" << endl;

    Info<< "Reading reference atmospheric pressure P_ref";  //coupling
    dimensionedScalar P_ref(transportProperties.lookup("P_ref"));
	Info<< " : " << P_ref.value() << "\n" << endl;
    
    Info<< "Reading reference air density rho_a_ref from SNOWPACK";
    dimensionedScalar rho_a_ref("rho_a_ref", dimDensity, Constants::density_air);
	Info<< " : " << rho_a_ref.value() << "\n" << endl;

    Info<< "Reading reference dynamic viscosity mu_a_ref";
    dimensionedScalar mu_a_ref(transportProperties.lookup("mu_a_ref"));
	Info<< " : " << mu_a_ref.value() << "\n" << endl;
        
    Info<< "Reading reference air specific heat capacity Cp_a_ref from SNOWPACK";
    dimensionedScalar Cp_a_ref("Cp_a_ref", dimSpecificHeatCapacity, Constants::specific_heat_air);
	Info<< " : " << Cp_a_ref.value() << "\n" << endl;

    Info<< "Reading reference water vapor specific heat capacity at constant pressure at 273.15K, Cp_v_ref";
    dimensionedScalar Cp_v_ref(transportProperties.lookup("Cp_v_ref"));
	Info<< " : " << Cp_v_ref.value() << "\n" << endl;
    
    Info<< "Reading reference ice specific heat capacity Cp_i_ref from SNOWPACK";
    dimensionedScalar Cp_i_ref("Cp_i_ref", dimSpecificHeatCapacity, Constants::specific_heat_ice);
	Info<< " : " << Cp_i_ref.value() << "\n" << endl;
    
    Info<< "Reading reference water specific heat capacity Cp_w_ref from SNOWPACK";
    dimensionedScalar Cp_w_ref("Cp_w_ref", dimSpecificHeatCapacity, Constants::specific_heat_water);
	Info<< " : " << Cp_w_ref.value() << "\n" << endl;
            
    Info<< "Reading thermal expansion coefficient of air beta_a_ref";
    dimensionedScalar beta_a_ref(transportProperties.lookup("beta_a_ref"));
	Info<< " : " << beta_a_ref.value() << "\n" << endl;
    
    Info<< "Reading thermal conductivity of the air component ka from SNOWPACK";
    dimensionedScalar ka("ka", dimPower/dimLength/dimTemperature, Constants::conductivity_air);
	Info<< " : " << ka.value() << "\n" << endl;

    Info<< "Reading thermal conductivity of the ice component ki from SNOWPACK";
    dimensionedScalar ki("ki", dimPower/dimLength/dimTemperature, Constants::conductivity_ice);
	Info<< " : " << ki.value() << "\n" << endl;
            
    Info<< "Reading thermal conductivity of the water component kw from SNOWPACK";
    dimensionedScalar kw("kw", dimPower/dimLength/dimTemperature, Constants::conductivity_water);
	Info<< " : " << kw.value() << "\n" << endl;
            
    Info<< "Reading latent heat of sublimation, ice to water vapor, Liv from SNOWPACK";
    dimensionedScalar Liv("Liv", dimEnergy/dimMass, Constants::lh_sublimation);
	Info<< " : " << Liv.value() << "\n" << endl;

    Info<< "Reading latent heat of fusion, ice to liquid water, Liw from SNOWPACK";
    dimensionedScalar Liw("Liw", dimEnergy/dimMass, Constants::lh_fusion);
	Info<< " : " << Liw.value() << "\n" << endl;

    Info<< "Reading latent heat of evaporation, liquid water to water vapor, Lwv from SNOWPACK";
    dimensionedScalar Lwv("Lwv", dimEnergy/dimMass, Constants::lh_vaporization);
	Info<< " : " << Lwv.value() << "\n" << endl;
            
    Info<< "Reading water vapor diffusion coefficient in air, Dv_a from SNOWPACK";
    dimensionedScalar Dv_a("Dv_a", dimLength*dimLength/dimTime, Constants::diffusion_coefficient_in_air);
	Info<< " : " << Dv_a.value() << "\n" << endl;

    Info<< "Reading gravitational acceleration gravity from SNOWPACK";
    dimensionedVector gravity("gravity", dimAcceleration, vector(0,0,-Constants::g));
	Info<< " : " << gravity.value() << "\n" << endl;
        
    Info<< "Reading the minimum volumetric ice content allowed, min_ice_content from SNOWPACK";
    dimensionedScalar min_ice_content("min_ice_content", dimless, Snowpack::min_ice_content);
	Info<< " : " << min_ice_content.value() << "\n" << endl;
    
    Info<< "Reading the maximum volumetric ice content allowed, max_ice_content from SNOWPACK";
    dimensionedScalar max_ice_content("max_ice_content", dimless, ReSolver1d::max_theta_ice * (1. - Constants::eps));
	Info<< " : " << max_ice_content.value() << "\n" << endl;
    
    Info<< "Reading the minimum volumetric water content allowed, min_water_content from SNOWPACK";
    dimensionedScalar min_water_content("min_water_content", dimless, 0.);
	Info<< " : " << min_water_content.value() << "\n" << endl;
    
    Info<< "Reading the maximum volumetric water content allowed, max_water_content from SNOWPACK";
    dimensionedScalar max_water_content("max_water_content", dimless, 1.);
	Info<< " : " << max_water_content.value() << "\n" << endl;
    
    Info<< "Reading the minimum volumetric air content allowed, min_air_content from SNOWPACK";
    dimensionedScalar min_air_content("min_air_content", dimless, 0.);
	Info<< " : " << min_air_content.value() << "\n" << endl;
    
    Info<< "Reading the maximum volumetric air content allowed, max_air_content from SNOWPACK";
    dimensionedScalar max_air_content("max_air_content", dimless, 1.);
	Info<< " : " << max_air_content.value() << "\n" << endl;
    
    Info<< "Reading the density of pure ice, rho_ice from SNOWPACK";
    dimensionedScalar rho_ice("rho_ice", dimDensity, Constants::density_ice);
	Info<< " : " << rho_ice.value() << "\n" << endl;
        
    Info<< "Reading the density of pure water, rho_water from SNOWPACK";  //coupling
    dimensionedScalar rho_water("rho_water", dimDensity, Constants::density_water);
	Info<< " : " << rho_water.value() << "\n" << endl;
                
    Info<< "Reading the reference enthalpy of the water vapor component, J/kg, hv_ref";
    dimensionedScalar hv_ref( transportProperties.lookup("hv_ref"));
	Info<< " : " << hv_ref.value() << "\n" << endl;
    
    Info<< "Reading the reference enthalpy of the ice phase, J/kg, hi_ref";
    dimensionedScalar hi_ref(transportProperties.lookup("hi_ref"));
	Info<< " : " << hi_ref.value() << "\n" << endl;
    
    Info<< "Reading the coupling interval between heat transfer equations of gas and ice phase, couplingInterval";
    scalar couplingInterval = readScalar(transportProperties.lookup("couplingInterval"));
	Info<< " : " << couplingInterval << "\n" << endl;
    
    Info<< "Reading the characteristic length scale, height, H";
    dimensionedScalar H( transportProperties.lookup("H"));
	Info<< " : " << H.value() << "\n" << endl;
       		
    Info<< "Reading the flag to archive all time folders in each processor folder, ToArchive";
    bool ToArchive=false;
	transportProperties.lookup("ToArchive") >> ToArchive;       
	Info<< " : " << ToArchive << "\n" << endl;
	
    Info<< "Reading the factor to turn mass change on/off, activeMassChange";
    scalar activeMassChange=1.0;
	transportProperties.lookup("activeMassChange") >> activeMassChange;
    if (activeMassChange!= 0.0 && activeMassChange!= 1.0)
    {
        FatalErrorIn
        (
            "in file createFields.H:\n"
            "line:\n"
				"transportProperties.lookup(\"activeMassChange\") >> activeMassChange;\n"
        )   << "activeMassChange should be either 1.0 or 0.0 not any thing elese" << nl << exit(FatalError);
    }	      
	Info<< " : " << activeMassChange << "\n" << endl;	
	 
    Info<< "Reading the molecular mass of dry air, Mair";
	dimensionedScalar Mair("Mair", dimMass/dimMoles, 28.9647e-3);
	Info<< " : " << Mair.value() << "\n" << endl;

    Info<< "Reading the molecular mass of water vapor, Mvap";	 	
	dimensionedScalar Mvap("Mvap", dimMass/dimMoles, 18.02e-3);     
	Info<< " : " << Mvap.value() << "\n" << endl;

    Info<< "Reading the universal gas constant, gaz_constant";	 	
	dimensionedScalar gaz_constant("gaz_constant", dimEnergy/dimMoles/dimTemperature, Constants::gas_constant_mol); 
	Info<< " : " << gaz_constant.value() << "\n" << endl;
	

    Info<< "Reading the triplet point temperature for water, triple_point_t";	 	
	scalar triple_point_t = Constants::triple_point_t; // (K)
	Info<< " : " << triple_point_t << "\n" << endl;
	
    Info<< "Reading the triplet point temperature for water, triple_point_p";	 	
	scalar triple_point_p = Constants::triple_point_p; // (Pa)
	Info<< " : " << triple_point_p << "\n" << endl;

    Info<< "Reading the minimum thickness under which the cell collapses, minThickness_dynMesh";
    scalar minThickness_dynMesh=0.0;
	transportProperties.lookup("minThickness_dynMesh") >> minThickness_dynMesh;
	Info<< " : " << minThickness_dynMesh << "\n" << endl;	
					
    Info<< "Reading the maximum thickness above which the cell splits, maxThickness_dynMesh";
    scalar maxThickness_dynMesh=0.0;
	transportProperties.lookup("maxThickness_dynMesh") >> maxThickness_dynMesh;
	Info<< " : " << maxThickness_dynMesh << "\n" << endl;	
		
    Info<< "Reading the initial cell size in x direction OpenFOAM:, initialCellSize_x";
    scalar initialCellSize_x=0.0;
	transportProperties.lookup("cellsizeX") >> initialCellSize_x;
	Info<< " : " << initialCellSize_x << "\n" << endl;	

    Info<< "Reading the initial cell size in z direction OpenFOAM:, initialCellSize_z";
    scalar initialCellSize_z=0.0;
	transportProperties.lookup("cellsizeZ") >> initialCellSize_z;
	Info<< " : " << initialCellSize_z << "\n" << endl;
	
    Info<< "Reading the domain size in x direction OpenFOAM:, L_x";
    scalar L_x=0.0;
	transportProperties.lookup("Lx") >> L_x;
	Info<< " : " << L_x << "\n" << endl;
	
    //Info<< "Reading the number of cells in x direction OpenFOAM:, number_cell_x_OF";
    //scalar number_cell_x_OF=0.0;
	//transportProperties.lookup("number_cell_x_OF") >> number_cell_x_OF;
	//Info<< " : " << number_cell_x_OF << "\n" << endl;
	
	scalar byPass_threshold=900.; /// Jafari added for by-pass
	Info<< "Reading the threshold after which the flow fileds solving get stoppted to avoid computational time";
	transportProperties.lookup("byPass_threshold") >> byPass_threshold;
	Info<< " : " << byPass_threshold << "\n" << endl;	
	
	/// this can be used for retrieving it anywhere provided that #include "IOList.H"
	///	is included wherever we use it if it is needed
	IOList<scalar> topPatchDisplacement
	(
		IOobject
		(
			 "topPatchDisplacement",
			 runTime.constant(),
			 mesh,
			 IOobject::NO_READ,
			 IOobject::NO_WRITE
		 ),
		1//<sizeOfWishedScalarList<
	);
	topPatchDisplacement[0] = 0.;
					
/////////////////////////////////////////////////////////////////////
//const pointField& ctrs = mesh.cellCentres();

// J commented
//Info<< "Reading thermophysical properties\n" << endl;

//autoPtr<rhoThermo> pThermo(rhoThermo::New(mesh));
//rhoThermo& thermo = pThermo();
//thermo.validate(args.executable(), "h", "e");
// J commented

Info<< "Creating field, air density, rho_a\n" << endl;
volScalarField rho_a
(
	IOobject
	(
		"rho_a",
		runTime.timeName(),
		mesh,
		IOobject::MUST_READ,
		IOobject::AUTO_WRITE
	),
	mesh//,
	//dimensionedScalar("rho_a", dimDensity, rho_a_ref.value()),
	//zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, water vapor density, rho_v\n" << endl;	
volScalarField rho_v
(
	IOobject
	(
		"rho_v",
		runTime.timeName(),
		mesh,
		IOobject::MUST_READ,
		IOobject::AUTO_WRITE
	),
	mesh
);

Info<< "Creating field, gas mixture (humid air) density, rho \n" << endl;	    
volScalarField rho
(
    IOobject
    (
        "rho",
        runTime.timeName(),
        mesh,
        IOobject::MUST_READ,
        IOobject::AUTO_WRITE
    ),
	mesh    
	//dimensionedScalar("rho", dimDensity, 0),
	//zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, gas mixture (humid air) density to check mass continuity error, rho_check \n" << endl;	    
volScalarField rho_check
(
    IOobject
    (
        "rho_check",
        runTime.timeName(),
        mesh,
        IOobject::NO_READ,
        IOobject::NO_WRITE
    ),
	mesh,    
	dimensionedScalar("rho_check", dimDensity, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating/Reading field, saturation water vapor density, rho_vs\n" << endl;	
volScalarField rho_vs
(
	IOobject
	(
		"rho_vs",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("rho_vs", dimDensity, 0),
	zeroGradientFvPatchScalarField::typeName
);   
 
Info<< "Creating field, the first derivative of saturation water vapor density with respect to T, drho_vs_dT\n" << endl;	
volScalarField drho_vs_dT
(
	IOobject
	(
		"drho_vs_dT",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("drho_vs_dT", dimDensity/dimTemperature, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the rate of mass phase change of water vapor, total, rv\n" << endl;	
volScalarField rv
(
	IOobject
	(
		"rv",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("rv", dimMass/dimVolume/dimTime, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the rate of mass sublimation/deposition of water vapor, miv\n" << endl;  //coupling
volScalarField miv
(
	IOobject
	(
		"miv",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("miv", dimMass/dimVolume/dimTime, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the rate of mass evaporation/condensation of water vapor, mwv\n" << endl; //coupling
volScalarField mwv
(
	IOobject
	(
		"mwv",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("mwv", dimMass/dimVolume/dimTime, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the accumulation of the ice content change due to water vapor transport in OpenFOAM, positive means deposition, deltaThetaIce_accum\n" << endl; //coupling
volScalarField deltaThetaIce_accum
(
	IOobject
	(
		"deltaThetaIce_accum",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("deltaThetaIce_accum", dimless, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the accumulation of the water content change due to water vapor transport in OpenFOAM, positive means condensation, deltaThetaWater_accum\n" << endl; //coupling
volScalarField deltaThetaWater_accum
(
	IOobject
	(
		"deltaThetaWater_accum",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("deltaThetaWater_accum", dimless, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the accumulation of density change due to water vapor transport in OpenFOAM, positive means condensation/deposition, snowDen_accum\n" << endl; //coupling
volScalarField snowDen_accum
(
	IOobject
	(
		"snowDen_accum",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("snowDen_accum", dimDensity, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the rate of energy per unit volume absorbed by shortwave radiation, q_swr\n" << endl; //coupling
volScalarField q_swr
(
	IOobject
	(
		"q_swr",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("q_swr", dimEnergy/dimVolume/dimTime, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the rate of energy per unit volume caused by melting/refreezing for sea ice module, q_Qph\n" << endl; //coupling
volScalarField q_Qph
(
	IOobject
	(
		"q_Qph",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("q_Qph", dimEnergy/dimVolume/dimTime, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the rate of energy per unit volume caused by melting/refreezing, q_Qmf\n" << endl; //coupling
volScalarField q_Qmf
(
	IOobject
	(
		"q_Qmf",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("q_Qmf", dimEnergy/dimVolume/dimTime, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the specific heat capacity for humid air density Cp_g\n" << endl;
volScalarField Cp_g
(
	IOobject
	(
		"Cp_g",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("Cp_g", dimSpecificHeatCapacity, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);
	
Info<< "Creating/Reading field, voidFraction\n" << endl;
volScalarField voidFraction
(
	IOobject
	(
		"voidFraction",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("voidFraction", dimless, 0),
	zeroGradientFvPatchScalarField::typeName
);
voidFraction.correctBoundaryConditions();

Info<< "Creating/Reading field, iceFraction\n" << endl; //coupling
volScalarField iceFraction
(
	IOobject
	(
		"iceFraction",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("iceFraction", dimless, 0),
	zeroGradientFvPatchScalarField::typeName
);
iceFraction.correctBoundaryConditions();

Info<< "Creating/Reading field, waterFraction\n" << endl; //coupling
volScalarField waterFraction
(
	IOobject
	(
		"waterFraction",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("waterFraction", dimless, 0),
	zeroGradientFvPatchScalarField::typeName
);
waterFraction.correctBoundaryConditions();

Info<< "Creating field, the specific Surface area (1/m), as_ \n" << endl;	
volScalarField as_
(
	IOobject
	(
		"as_",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE //J added
	),
	mesh,
	dimensionedScalar("as_", dimless/dimLength, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the snow grain size diameter, dp_ \n" << endl;	
volScalarField dp_
(
	IOobject
	(
		"dp_",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE //J added
	),
	mesh,
	dimensionedScalar("dp_", dimLength, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the optical snow grain size diameter, ogs_ \n" << endl;	
volScalarField ogs_
(
	IOobject
	(
		"ogs_",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE //J added
	),
	mesh,
	dimensionedScalar("ogs_", dimLength, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the snow grain size diameter, dp_ \n" << endl;	
volScalarField check_
(
	IOobject
	(
		"check_",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("check_", dimless, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, hm*as, hm= mass transfer coefficient (m/s) and as specific Surface area (1/m) \n" << endl;	
volScalarField hmas
(
	IOobject
	(
		"hmas",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("hmas", dimless/dimTime, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, hc*as, hc= the convective heat transfer coefficient (m/s) and as specific Surface area (1/m) \n" << endl;	
volScalarField hcas
(
	IOobject
	(
		"hcas",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("hcas", dimPower/dimVolume/dimTemperature, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Reading field, the ice/water phase temperature, Ti. the thermal equiliobruim between ice and water is assimued when both present.\n" << endl;
volScalarField Ti
(
	IOobject
	(
		"Ti",
		runTime.timeName(),
		mesh,
		IOobject::MUST_READ,
		IOobject::AUTO_WRITE
	),
	mesh
);

Info<< "Reading field, the humid air temperature, Tg\n" << endl;
volScalarField Tg
(
	IOobject
	(
		"Tg",
		runTime.timeName(),
		mesh,
		IOobject::MUST_READ,
		IOobject::AUTO_WRITE
	),
	mesh
);

Info<< "Creating field, the averaged temperature, Tm\n" << endl;
volScalarField Tm
(
	IOobject
	(
		"Tm",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("Tm", dimTemperature, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);
Tm=0.5*(Tg+Ti);

Info<< "Creating field, the melting point temperature, meltfreeze_tk_OF\n" << endl;
volScalarField meltfreeze_tk_OF
(
	IOobject
	(
		"meltfreeze_tk_OF",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("meltfreeze_tk_OF", dimTemperature, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the compressibility psi=1/(Rg*T)\n" << endl;
volScalarField psi
(
	IOobject
	(
		"psi",
		mesh.time().timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("psi", dimCompressibility, 0)//,
	//zeroGradientFvPatchScalarField::typeName	
);

Info<< "Creating field, the mass-based weight of humid air for interface temperature, wg: T_I=wg*Tg+wi*Ti, T_I is the humid air-ice temperature \n" << endl;	
volScalarField wg
(
	IOobject
	(
		"wg",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("wg", dimless, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, mass-based weight of ice phase for interface temperature, wi: T_I=wg*Tg+wi*Ti, T_I is the humid air-ice temperature \n" << endl;	
volScalarField wi
(
	IOobject
	(
		"wi",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("wi", dimless, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the effective thermal conductivity of humid air in snow \n" << endl;	
volScalarField keff_ha
(
	IOobject
	(
		"keff_ha",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE //J added
	),
	mesh,
	dimensionedScalar("keff_ha", dimPower/dimLength/dimTemperature, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the effective thermal conductivity of ice in snow \n" << endl;	
volScalarField keff_i
(
	IOobject
	(
		"keff_i",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE //J added
	),
	mesh,
	dimensionedScalar("keff_i", dimPower/dimLength/dimTemperature, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the effective thermal conductivity of ice in snow \n" << endl;	//coupling
volScalarField keff_w
(
	IOobject
	(
		"keff_w",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE //J added
	),
	mesh,
	dimensionedScalar("keff_w", dimPower/dimLength/dimTemperature, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the effective thermal conductivity of snow \n" << endl;	
volScalarField keffs
(
	IOobject
	(
		"keffs",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("keffs", dimPower/dimLength/dimTemperature, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, intrinsic permeability K_ \n" << endl;	
volScalarField K_
(
	IOobject
	(
		"K_",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE //J added
	),
	mesh,
	dimensionedScalar("K_", dimLength*dimLength, 0),
	zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the molar mass of humid air is Mg kg/mol \n" << endl;	
volScalarField Mg
(
	IOobject
	(
		"Mg",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::NO_WRITE
	),
	mesh,
	dimensionedScalar("Mg", dimMass/dimMoles, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the effective water vapor density in snow, Deff \n" << endl;	
volScalarField Deff
(
	IOobject
	(
		"Deff",
		runTime.timeName(),
		mesh,
		IOobject::NO_READ,
		IOobject::AUTO_WRITE //J added
	),
	mesh,
	dimensionedScalar("Deff", dimLength*dimLength/dimTime, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);
        	
//volScalarField& p = thermo.p(); // J commented

Info<< "Reading field U\n" << endl;
volVectorField U
(
    IOobject
    (
        "U",
        runTime.timeName(),
        mesh,
        IOobject::MUST_READ,
        IOobject::AUTO_WRITE
    ),
    mesh
);

#include "compressibleCreatePhi.H"

// J commented
//Info<< "Creating turbulence model\n" << endl;
//autoPtr<compressible::turbulenceModel> turbulence
//(
    //compressible::turbulenceModel::New
    //(
        //rho,
        //U,
        //phi,
        //thermo
    //)
//);
// J commented

#include "readGravitationalAcceleration.H"
#include "readhRef.H"
#include "gh.H"


Info<< "Reading field p\n" << endl;
volScalarField p
(
    IOobject
    (
        "p",
        runTime.timeName(),
        mesh,
        IOobject::MUST_READ,
        IOobject::AUTO_WRITE
    ),
    mesh
);

Info<< "Reading field p_rgh\n" << endl;
volScalarField p_rgh
(
    IOobject
    (
        "p_rgh",
        runTime.timeName(),
        mesh,
        IOobject::MUST_READ,
        IOobject::AUTO_WRITE
    ),
    mesh
);

// Force p_rgh to be consistent with p
p_rgh = p - rho*gh; // ?

label pRefCell = 0;
scalar pRefValue = 0.0;

// J commented
//if (thermo.incompressible())
//{
    setRefCell
    (
        p,
        p_rgh,
        pimple.dict(),
        pRefCell,
        pRefValue
    );
//}
// J commented
//Info << pRefCell << ' ' << pRefValue << ' ' << min(p).value() << ' ' << min(p_rgh).value() << endl;

/*
p += dimensionedScalar
(
   "p",
    p.dimensions(),
    pRefValue - getRefCellValue(p, pRefCell)
);
// Force p_rgh to be consistent with p
p_rgh = p - rho*gh;
*/

mesh.setFluxRequired(p_rgh.name());

Info << pRefCell << ' ' << pRefValue << ' ' << min(p).value() << ' ' << min(p_rgh).value() << endl;


Info<< "Creating field dPorosity_Pdt\n" << endl;
volScalarField Porosityd_Pdt
(
    IOobject
    (
        "Porosityd_Pdt",
        runTime.timeName(),
        mesh
    ),
    mesh,
    dimensionedScalar("Porosityd_Pdt", p.dimensions()/dimTime, 0)
);

//Info<< "Creating field kinetic energy K\n" << endl; // J commented
//volScalarField K("K", 0.5*magSqr(U)); // J commented

volVectorField gradTm 
(
	IOobject
    (
		"gradTm",
        runTime.timeName(),
        mesh,
        IOobject::NO_READ,
        IOobject::AUTO_WRITE
    ),
    mesh,
    dimensionedVector("gradTm",Ti.dimensions()/dimLength, Zero)//,
    //fixedValueFvPatchVectorField::typeName
);
//gradTm=fvc::grad(Tm);

Info<< "Creating field, the effective diffusive vapor flux, Jv \n" << endl;	
volVectorField Jv // needed based on revised formulation for gas phase heat transfer
(
	IOobject
	(
		"Jv",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedVector("Jv", dimMass/dimArea/dimTime, Zero)//,
	//zeroGradientFvPatchScalarField::typeName
);

Info<< "Creating field, the Rayleigh number based on H=1, Ra_ \n" << endl;	
volScalarField Ra_ // needed based on revised formulation for gas phase heat transfer
(
	IOobject
	(
		"Ra_",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("Ra_", dimless, 0)//,
	//zeroGradientFvPatchScalarField::typeName
);

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Jafari added: for het
Info<< "Creating field, the indicator function, Indicator_ \n" << endl;	
volScalarField Indicator_
(
	IOobject
	(
		"Indicator_",
		runTime.timeName(),
		mesh,
		IOobject::READ_IF_PRESENT,
		IOobject::AUTO_WRITE
	),
	mesh,
	dimensionedScalar("Indicator_", dimless, 1),
	zeroGradientFvPatchScalarField::typeName
);
Indicator_.correctBoundaryConditions();

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// archived procedures
#include "createArchiving.H" 
     
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
#include "createMRF.H"
//#include "createRadiationModel.H" // J commented
//#include "settingInitialCondition.H" // for coupling

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//label patchIdTop = mesh.boundaryMesh().findPatchID("top");
//label patchIdBottom= mesh.boundaryMesh().findPatchID("bottom");

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//#include "createSNOWPACK.H"
//bool doNextStep_SN=true;
//bool doNextStep_OF=true;
//scalar cum_OF_deltaT=0.;
//scalar SN_deltaT=M_TO_S(coupling.calculation_step_length);
//scalar snowHeight_SN=0.0;

/// Note: most of ddt operators and maybe others for dynamicMesh use mesh.Phi() and also old sub-cycle volume Vsc0.
/// Hence, to consider fixed-mesh strategy for OpenFOAM-SNOWPACK coupling we need to set mesh.phi() as zero and avoid using
/// Vsc0 instead of Vsc. Check EulerDdtScheme.C for example. First, we need public boolean movingMeshReal to do the dynamic mesh stuff, then by using
/// mesh.moving(false), we use the fixed-mesh stretaegy. Note that by mesh.moving(false) at the end we just use the fixed-mesh strategy 
/// hoewver, we need at the start of dynMesh loop to use mesh.moving(movingMeshReal) to do add/remove mesh layers because it was set previously falsely false. 			
bool movingMeshReal=mesh.moving();			
