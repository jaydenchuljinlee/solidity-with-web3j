// SPDX-License-Identifier: MIT
pragma solidity ^0.8.18;

import "@openzeppelin/contracts/token/ERC20/ERC20.sol";

contract Token is ERC20 {
    address private ownerAddress;

    mapping(address => bool) private managerList;

    event ManagerAdded(address _address);
    event ManagerRemoved(address _address);

    modifier onlyOwner() {
        require(msg.sender == ownerAddress, "You are not owner.");
        _;
    }

    modifier onlyManager() {
        if (msg.sender != ownerAddress) {
            require(managerList[msg.sender] == true, "You are not manager.");
        }
        _;
    }

    constructor(string memory name, string memory symbol) ERC20(name, symbol) {
        ownerAddress = msg.sender;
    }

    function getOwnerAddress() public view returns (address) {
        return ownerAddress;
    }

    // Add Manager: 컨트렉트 Manager(함수 호출 허용 권한) 목록에 허용 주소 추가
    function doAddManager(address _address) external onlyOwner returns (address) {
        managerList[_address] = true;

        emit ManagerAdded(_address);

        return _address;
    }

    // Remove Manager: 컨트렉트 Manager(함수 호출 허용 권한) 목록에 허용 주소 제거
    function doRemoveManager(address _address) external onlyOwner returns (address) {
        delete managerList[_address];

        emit ManagerRemoved(_address);

        return _address;
    }

    // Token Mint: 컨트렉트 Owner, Manager 에 한해 메소드 호출 허용
    function mint(address to, uint256 amount) external onlyManager {
        _mint(to, amount);
    }

    // Token Burn: 컨트렉트 Owner, Manager 에 한해 메소드 호출 허용
    function burn(address to, uint256 amount) external onlyManager {
        _burn(to, amount);
    }

    function decimals() public pure override returns (uint8) {
        return 0;
    }
}