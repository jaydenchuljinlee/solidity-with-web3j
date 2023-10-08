# 1. Solidity + Web3j Project
- 프로젝트 설명
  - truffle / hardhat 기반의 Web3 프로젝트처럼 하나의 모듈 안에서 Compile과 Deploy를 한다.
  - 컴파일은 테스트를 통해 실행하고, 컴파일 된 파일은 프로젝트 root의 build/contracts/{contract_prefix} 내에 위치한다.

- Issues
  - Contract 내에서 npm packages로 구현하던 dependency들을 어떻게 처리할지

