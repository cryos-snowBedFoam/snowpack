/*---------------------------------------------------------------------------*\
  =========                 |
  \\      /  F ield         | OpenFOAM: The Open Source CFD Toolbox
   \\    /   O peration     |
    \\  /    A nd           | Copyright (C) 2011-2017 OpenFOAM Foundation
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

\*---------------------------------------------------------------------------*/

#include "topBoundaryLayerAdditionRemovalTopoFvMesh.H"
#include "Time.H"
#include "mapPolyMesh.H"
#include "layerAdditionRemoval.H"
#include "addToRunTimeSelectionTable.H"
#include "meshTools.H"
#include "OFstream.H"
#include "mathematicalConstants.H"

#include "volFields.H"
#include "scalarIOList.H"
#include "dimensionedScalar.H"

using namespace Foam::constant::mathematical;

// * * * * * * * * * * * * * * Static Data Members * * * * * * * * * * * * * //

namespace Foam
{
    defineTypeNameAndDebug(topBoundaryLayerAdditionRemovalTopoFvMesh, 0);

    addToRunTimeSelectionTable
    (
        topoChangerFvMesh,
        topBoundaryLayerAdditionRemovalTopoFvMesh,
        IOobject
    );
}


// * * * * * * * * * * * * * Private Member Functions  * * * * * * * * * * * //
void Foam::topBoundaryLayerAdditionRemovalTopoFvMesh::addZonesAndModifiers()
{
    // Add zones and modifiers for motion action
    if
    (
        pointZones().size()
     || faceZones().size()
     || cellZones().size()
     || topoChanger_.size()
    )
    {
		
        //InfoInFunction
        Info
            << "Zones and modifiers already present.  Skipping."
            << endl;

        return;
    }

	// this is to delete the face/point/cell zones 
	/*
	pointZoneMesh& pZ0=pointZones();
	pZ0.clear();
	faceZoneMesh& fZ0=faceZones();
	fZ0.clear();
	cellZoneMesh& cZ0=cellZones();
	cZ0.clear();
    // Write mesh
    write();
	*/

    Info<< "Time = " << time().timeName() << endl
        << "Adding zones and modifiers to the mesh" << endl;


    // Add zones
    List<pointZone*> pz(0);
    List<faceZone*> fz(1);
    List<cellZone*> cz(0);


    // Add face zone for layer addition
    const word boundaryPatchName
    (
        motionDict_.subDict("boundary").lookup("patch")
    );

    const polyPatch& boundaryPatch = boundaryMesh()[boundaryPatchName];

    labelList lpf(boundaryPatch.size());

    forAll(lpf, i)
    {
        lpf[i] = boundaryPatch.start() + i;
    }

    fz[0] = new faceZone
    (
        "topBoundaryZone",
        lpf,
        boolList(boundaryPatch.size(), true),//boolList(boundaryPatch.size(), true),
        0,
        faceZones()
    );


    Info<< "Adding point and face zones" << endl;
    addZones(pz, fz, cz);
    
	/*
    if
    (
       topoChanger_.size()
    )
    {
		
        //InfoInFunction
        Info
            << "Modifiers already present.  Skipping."
            << endl;

        return;
    }
    Info<< "Time = " << time().timeName() << endl
        << "Adding modifiers to the mesh" << endl;
	*/
	
    // Add a topology modifier

    List<polyMeshModifier*> tm(1);

    tm[0] =
        new layerAdditionRemoval
        (
            "topBoundaryAdditionRemovalModifier",
            0,
            topoChanger_,
            "topBoundaryZone",
            readScalar
            (
                motionDict_.subDict("boundary").lookup("minThickness")
            ),
            readScalar
            (
                motionDict_.subDict("boundary").lookup("maxThickness")
            )
        );


    Info<< "Adding topology modifiers" << endl;
    topoChanger_.addTopologyModifiers(tm);

    // Write mesh
    write();
}


// * * * * * * * * * * * * * * * * Constructors  * * * * * * * * * * * * * * //

Foam::topBoundaryLayerAdditionRemovalTopoFvMesh::topBoundaryLayerAdditionRemovalTopoFvMesh(const IOobject& io)
:
    topoChangerFvMesh(io),
    motionDict_
    (
        IOdictionary
        (
            IOobject
            (
                "dynamicMeshDict",
                time().constant(),
                *this,
                IOobject::MUST_READ_IF_MODIFIED,
                IOobject::NO_WRITE,
                false
            )
        ).optionalSubDict(typeName + "Coeffs")
    )
{
	addZonesAndModifiers();
}


// * * * * * * * * * * * * * * * * Destructor  * * * * * * * * * * * * * * * //

Foam::topBoundaryLayerAdditionRemovalTopoFvMesh::~topBoundaryLayerAdditionRemovalTopoFvMesh()
{}


// * * * * * * * * * * * * * * * Member Functions  * * * * * * * * * * * * * //

bool Foam::topBoundaryLayerAdditionRemovalTopoFvMesh::update()
{
    // Do mesh changes (use inflation - put new points in topoChangeMap)
    autoPtr<mapPolyMesh> topoChangeMap = topoChanger_.changeMesh(true);

    // Calculate the new point positions depending on whether the
    // topological change has happened or not
    
    if (topoChangeMap.valid())
    {
        Info<< "Topology change. Calculating motion points" << endl;

        if (topoChangeMap().hasMotionPoints())
        {
            Info<< "Topology change. Has premotion points" << endl;
			
			// Calculate the new point positions depending on whether the
			// topological change has happened or not
			pointField newPoints=topoChangeMap().preMotionPoints();

			// The mesh now contains the cells with zero volume
			Info << "Executing mesh motion" << endl;
			movePoints(newPoints);			           
        }
        else
        {
            Info<< "Topology change. Already set mesh points" << endl;

        }
    }
    else
    {
        Info<< "No topology change" << endl;
    }
 
 		//sthis->setPhi()=dimensionedScalar("zerrr", dimArea*dimVelocity,0);
       
    return true;
}


// ************************************************************************* //
