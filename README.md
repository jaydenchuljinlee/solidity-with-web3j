# :house: Solidity + Web3j Project

## :pushpin: Project Description
  - truffle / hardhat 기반의 Web3 프로젝트처럼 하나의 모듈 안에서 Compile과 Deploy를 한다.
  - sol 파일의 위치는 <b>src/solidity</b> 로 지정
  - 컴파일 된 sol 파일은 프로젝트 root의 <b>build/contracts/{contract_prefix}</b> 내에 위치한다.
  - 외부 NPM Packages는 <b>build/node_modules</b> 내에 위치한다.

## :pushpin: TODO Settings
  - NPM Packages의 경우 DSL로 directory 설정 필요
  - Contract Compile을 Contract Unit Test 시에 실행하여 테스트 안정성 확보 필요 

## :pushpin: Available Version
  - jdk 11
  - gradle 7.x
  - solidity ~ 0.8.19


