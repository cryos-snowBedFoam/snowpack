//rho = thermo.rho(); // J commented

// Thermodynamic density needs to be updated by psi*d(p) after the
// pressure solution
//const volScalarField psip0(psi*p); // J commented

#include "updateEquationOfState.H" // J added

const volScalarField psip0(psi*p);

volScalarField rAU(1.0/UEqn.A());
surfaceScalarField rhorAUf("rhorAUf", fvc::interpolate(rho*rAU));
volVectorField HbyA(constrainHbyA(rAU*UEqn.H(), U, p_rgh));

surfaceScalarField phig(-rhorAUf*ghf*fvc::snGrad(rho)*mesh.magSf());

surfaceScalarField phiHbyA
(
    "phiHbyA",
    (
        fvc::flux(rho*HbyA)
      + rhorAUf*fvc::ddtCorr(rho, U, phi)
    )
  + phig
);

MRF.makeRelative(fvc::interpolate(rho), phiHbyA);

// Update the pressure BCs to ensure flux consistency
constrainPressure(p_rgh, rho, U, phiHbyA, rhorAUf, MRF);

fvScalarMatrix p_rghDDtEqn
(
    //fvc::ddt(rho) + psi*correction(fvm::ddt(p_rgh)) // J commented
    //fvc::ddt(voidFraction,rho) + psi*voidFraction*correction(fvm::ddt(p_rgh)) //psi*voidFraction*correction(fvm::ddt(p_rgh))//psi*correction(fvm::ddt(voidFraction,p_rgh))//psi*correction(fvm::ddt(p_rgh)) J added
    fvm::ddt(voidFraction*psi, p_rgh) + fvc::ddt(voidFraction*psi, rho)*gh  //new    
    //fvm::ddt(voidFraction*psi, p_rgh) + fvc::ddt(voidFraction*psi, rho)*gh + fvc::ddt(voidFraction, psi)*dimensionedScalar("p", p.dimensions(), pRefValue - getRefCellValue(p, pRefCell))//new    
  + fvc::div(phiHbyA)
  ==
    rv // J added
  + fvOptions(psi, p_rgh, rho.name())
);

while (pimple.correctNonOrthogonal())
{
    fvScalarMatrix p_rghEqn
    (
        p_rghDDtEqn
      - fvm::laplacian(rhorAUf, p_rgh)
    );

	// J commented
    //if (thermo.incompressible())
    //{
        //p_rghEqn.setReference(pRefCell, getRefCellValue(p_rgh, pRefCell));
    //}
    
    p_rghEqn.solve(mesh.solver(p_rgh.select(pimple.finalInnerIter())));

    if (pimple.finalNonOrthogonalIter())
    {
        // Calculate the conservative fluxes
        phi = phiHbyA + p_rghEqn.flux();

        // Explicitly relax pressure for momentum corrector
        p_rgh.relax();

        // Correct the momentum source with the pressure gradient flux
        // calculated from the relaxed pressure
        U = HbyA + rAU*fvc::reconstruct((phig + p_rghEqn.flux())/rhorAUf);
        U.correctBoundaryConditions();
        fvOptions.correct(U);
        //K = 0.5*magSqr(U); // J commented
    }
}

//1st scenario
//Jafari added to ensure the pressure is around 1atm
p = p_rgh + rho*gh;
p += dimensionedScalar
(
   "p",
    p.dimensions(),
    //pRefValue-max(p).value()
    pRefValue - getRefCellValue(p, pRefCell)
);
p_rgh= p - rho*gh;
//Jafari added to ensure the pressure is around 1atm


// Thermodynamic density update
//thermo.correctRho(psi*p - psip0);  // J commented
rho_check=rho+(psi*p - psip0); // J added

// J commented
//if (thermo.dpdt())
//{
    Porosityd_Pdt = voidFraction*fvc::ddt(p);
//}

#include "rhoEqn.H"
#include "compressibleContinuityErrs.H"

