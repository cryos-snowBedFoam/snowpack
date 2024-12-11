/*---------------------------------------------------------------------------*\
  =========                 |
  \\      /  F ield         | OpenFOAM: The Open Source CFD Toolbox
   \\    /   O peration     |
    \\  /    A nd           | Copyright (C) 2011 OpenFOAM Foundation
     \\/     M anipulation  |
-------------------------------------------------------------------------------
License
    This file is part of OpenFOAM.

    OpenFOAM is free software: you can redistribute it and/or modify it
    under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    OpenFOAM is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
    for more details.

    You should have received a copy of the GNU General Public License
    along with OpenFOAM.  If not, see <http://www.gnu.org/licenses/>.

Class
    Foam::topBoundaryLayerAdditionRemovalTopoFvMesh

Description
    1)Sample topoChangerFvMesh that moves the patch named "top" in z direction
    based on the hard-coded displacement implemented in solver e.ge newPimpleDyFoam.
    
    2)After accessing the points on patch "top" and calculating the corresponding
    displacements, we need to call mesh.movePoints() and mesh.update(). The following 
    code gives an example of how to accsess the point on patch and how to evaluate the
    corresponding displacements.

			label patchId = mesh.boundaryMesh().findPatchID("top");
			const polyPatch& patchPP = mesh.boundaryMesh()[patchId];
			const labelList& labelPatchPoints(patchPP.meshPoints());
			pointField meshPoints(mesh.points());
			Pout<< " number points " <<  meshPoints.size()	<< endl;
						
			forAll(labelPatchPoints,id)
			{
				meshPoints[labelPatchPoints[id]].z()+=subDeltaZ;
				curH=meshPoints[labelPatchPoints[id]].z();
			}
			mesh.movePoints(meshPoints);
			mesh.update();
	
	3) This sub-class works in parallel with some limitations:
		3-1) the mesh decomposiyion needs to be done in x or y direction.
		3-2) the number of cell should be equal for each subdomain in x and y drection.
		3-3) if we use the boundary type of cyclic, we need to use "constraints" of type "constraints" for cyclic patchs in decomposeParDict as:

			constraints
			{
				// Keep owner and neighbour on same processor for faces in patches
				// (only makes sense for cyclic patches. Not suitable for e.g.
				// cyclicAMI since these are not coupled on the patch level.
				// Use singleProcessorFaceSets for those.
				patches
				{
					type    preservePatches;
					// the name of patch either can be between "" or without "".
					// It works also just to put cyclic even though it is not the name of patch but it is the type.
					patches (cyclic);//(left right);("cyclic"); 
					enabled true;
				}   
			} 			
				
SourceFiles
    topBoundaryLayerAdditionRemovalTopoFvMesh.C

\*---------------------------------------------------------------------------*/

#ifndef topBoundaryLayerAdditionRemovalTopoFvMesh_H
#define topBoundaryLayerAdditionRemovalTopoFvMesh_H

#include "topoChangerFvMesh.H"
#include "motionSolver.H"

#include "PackedBoolList.H"


// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * //

namespace Foam
{

// Forward declaration of classes

/*---------------------------------------------------------------------------*\
                      Class topBoundaryLayerAdditionRemovalTopoFvMesh Declaration
\*---------------------------------------------------------------------------*/

class topBoundaryLayerAdditionRemovalTopoFvMesh
:
    public topoChangerFvMesh
{
    // Private data

        //- Motion dictionary
        dictionary motionDict_;

    // Private Member Functions

        //- Disallow default bitwise copy construct
        topBoundaryLayerAdditionRemovalTopoFvMesh(const topBoundaryLayerAdditionRemovalTopoFvMesh&);

        //- Disallow default bitwise assignment
        void operator=(const topBoundaryLayerAdditionRemovalTopoFvMesh&);


        //- Add mixer zones and modifiers
        void addZonesAndModifiers();

public:

    //- Runtime type information
    TypeName("topBoundaryLayerAdditionRemovalTopoFvMesh");


    // Constructors

        //- Construct from database
        explicit topBoundaryLayerAdditionRemovalTopoFvMesh(const IOobject& io);


    //- Destructor
    virtual ~topBoundaryLayerAdditionRemovalTopoFvMesh();


    // Member Functions

        //- Update the mesh for both mesh motion and topology change
        virtual bool update();
};


// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * //

} // End namespace Foam

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * //

#endif

// ************************************************************************* //
