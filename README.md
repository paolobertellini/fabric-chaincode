# Agreement network
A Hyperledger Fabric network with a java chaincode

Inspired from [Create java chaincode - medium tutorial](https://medium.com/coinmonks/how-to-create-a-java-chaincode-and-deploy-in-a-hyperledger-fabric-2-network-65199e5f645d) 


1. Start the network
`./network.sh up`

2. Create the channel
`./network.sh createChannel`

3. Deploy the chaincode
`./network.sh deployCC -l java`

4. Stop the network
`./network.sh down`

