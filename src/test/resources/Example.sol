// SPDX-License-Identifier: MIT
pragma solidity ^0.5.0;

contract Example {

  bytes32 public hash;
  uint public value;
  uint public counter;
  bool public fallbackTriggered;
  uint[] public numbers;

  event ExampleEvent(address indexed _from, uint num);
  event ContractAddressEvent(address _contract);
  event SpecialEvent();
  event NumberEvent(int numA, int indexed numB, address addrC, uint numD, uint);

  constructor(uint val) public {
    // Constructor revert
    require(val != 13);
    require(val != 2001, 'reasonstring');
    require(val != 20001, 'solidity storage is a fun lesson in endianness');

    // Expensive deployment
    if(val >= 50){
      for(uint i = 0; i < val; i++){
        counter = i;
      }
    }

    value = val;
    fallbackTriggered = false;
  }

  function setValue(uint val) public {
    value = val;
  }

  function setNumbers(uint[] memory vals) public {
    numbers = vals;
  }

  function getNumbers() public view returns (uint [] memory vals){
    return numbers;
  }

  function isDeployed() public view returns (address){
    return address(this);
  }

  function viewSender() public view returns(address){
    return msg.sender;
  }

  function getValue() public view returns(uint) {
    return value;
  }

  function getValuePlus(uint toAdd) public view returns(uint) {
    return value + toAdd;
  }

  function overloadedGet() public view returns(uint){
    return value;
  }

  function overloadedGet(uint multiplier) public view returns(uint){
    return value * multiplier;
  }

  function overloadedSet(bytes32 h, uint val) public {
    hash = h;
    value = val;
  }

  function overloadedSet(bytes32 h, uint val, uint multiplier) public {
    hash = h;
    value = val * multiplier;
  }

  function triggerEvent() public {
    emit ExampleEvent(msg.sender, 8);
  }

  function triggerEventWithArgument(uint arg) public {
    emit ExampleEvent(msg.sender, arg);
  }

  function triggerSpecialEvent() public {
    emit SpecialEvent();
  }

  function triggerContractAddressEvent() public {
    emit ContractAddressEvent(address(this));
  }

  function triggerNumberEvent(int a, int b, address c, uint d, uint e) public {
    emit NumberEvent(a,b,c,d,e);
  }

  function runsOutOfGas() public {
    consumesGas();
  }

  function isExpensive(uint val) public {
    for(uint i = 0; i < val; i++){
      counter = i;
    }
  }

  function consumesGas() public {
    for(uint i = 0; i < 100000; i++){
      counter = i;
    }
  }

  function() external payable {
    fallbackTriggered = true;
  }
}