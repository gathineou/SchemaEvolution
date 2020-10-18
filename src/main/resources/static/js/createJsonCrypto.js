function AddNodes(data){
    // console.log(data)
    for (var i = 0; i < data.length; i++) {
        if(Array.isArray(data[i])){
            AddNodes(data[i])
        }
        else{
            cy.add([
                {group: "nodes", data: {id: data[i]["name"],name: data[i]["name"]}}
            ])
        }
    } 
}




function AddEdges(data){
    console.log("HELLo")
    console.log(data)
    for (var i = 0; i < data.length; i++){
        cy.add([
            { group: "edges", data: { source: data[i]["sourceSchema"], target: data[i]["destinationSchema"]}}
        ])
        // AddEdges(data[i]);
    }
}