# Agreement network
A Hyperledger Fabric network with a java chaincode

Inspired from [Create java chaincode - medium tutorial](https://medium.com/coinmonks/how-to-create-a-java-chaincode-and-deploy-in-a-hyperledger-fabric-2-network-65199e5f645d) 

## Setup

1. Start the network
`./network.sh up`

2. Create the channel
`./network.sh createChannel`

3. Deploy the chaincode
`./network.sh deployCC -ccn agreements -ccp ../chaincode/java/agreements/ -ccl java -cci initLedger`

4. Stop the network
`./network.sh down`

## Environment variables for Org1 and Org2

Org 1 <br>
`export PATH=${PWD}/../bin:$PATH`<br>
`export FABRIC_CFG_PATH=$PWD/../config/`<br>
`export CORE_PEER_TLS_ENABLED=true`<br>
`export CORE_PEER_LOCALMSPID="Org1MSP"`<br>
`export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt`<br>
`export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp`<br>
`export CORE_PEER_ADDRESS=localhost:7051`<br>

Org 2 <br>
`export PATH=${PWD}/../bin:$PATH`<br>
`export FABRIC_CFG_PATH=$PWD/../config/`<br>
`export CORE_PEER_TLS_ENABLED=true`<br>
`export CORE_PEER_LOCALMSPID="Org2MSP"`<br>
`export CORE_PEER_TLS_ROOTCERT_FILE=${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt`<br>
`export CORE_PEER_MSPCONFIGPATH=${PWD}/organizations/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp`<br>
`export CORE_PEER_ADDRESS=localhost:9051`<br>


## Interacting with the network

- query `peer chaincode  query -C mychannel -n agreements -c '{"Args":["getAgreement", "ARG001"]}'`
- invoke `peer chaincode invoke -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" -C mychannel -n agreements --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt" -c '{"function":"createAgreement","Args":["ARG002","paolo","andrea","open"]}'
`
