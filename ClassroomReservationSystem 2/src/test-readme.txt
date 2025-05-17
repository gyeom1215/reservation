실행 방법:
Maven 프로젝트로 설정되어 있는지 확인합니다 (pom.xml이 프로젝트 루트에 있어야 함).
터미널 또는 IDE에서 Maven 테스트를 실행합니다.
터미널: 프로젝트 루트 디렉토리에서 mvn test 명령어 실행
IDE (IntelliJ, Eclipse 등): 테스트 클래스나 메소드에서 마우스 오른쪽 버튼 클릭 후 "Run Tests" 선택

주요 검증 포인트 및 테스트 내용:
파일 시스템 (JSON) 기반 데이터 처리 검증:
JsonDataHandlerTest: 빈 리스트 저장/로드, 여러 예약 저장/로드, 존재하지 않는 파일 로드 시 동작 검증.
ReservationServiceTest 및 ReservationSystemIntegrationTest: 예약 생성/취소 시 jsonDataHandler를 통해 실제 파일에 변경 사항이 반영되는지 간접적(새 서비스 인스턴스 로드) 또는 직접적(테스트용 파일 내용 확인)으로 검증. @TempDir로 테스트마다 격리된 파일 사용.
사용자 권한 분기 (학생 기능 중심):
ReservationServiceTest의 cancelReservation_NotOwnReservation_ShouldFail(): 서비스 레벨에서 다른 학생 ID로 취소 시도 시 실패하는지 확인.
ReservationSystemIntegrationTest의 studentCannotCancelOthersReservation_Integration(): 통합 관점에서 다른 학생 예약 취소 시도가 파일 상태에 영향을 주지 않는지 확인.
승인 조건 판단 (시간 겹침 등):
ReservationServiceTest의 makeReservation_TimeConflict(), makeReservation_SameStartAndEndTime_ShouldFail(), makeReservation_StartTimeAfterEndTime_ShouldFail(), isTimeSlotAvailable_VariousScenarios(): 다양한 시간 조건에 따른 예약 가능/불가능 로직 검증.
ReservationSystemIntegrationTest의 conflictingReservations_SecondAttemptFails_FileSystemCheck(): 시간 겹침으로 예약 실패 시 파일에 반영되지 않음을 확인.
UI 상태 변화 (서비스 계층 검증으로 대체):
UI 로직을 담당하는 ReservationService의 메소드들이 예상대로 동작하는지 (예: getMyReservations가 올바른 목록 반환) 테스트하여 간접적으로 UI에 표시될 데이터의 정확성을 검증합니다.
예약 성공/실패 여부에 따라 UI에 다른 메시지가 표시될 텐데, 이 로직의 기반이 되는 서비스 메소드의 boolean 반환 값 등을 테스트합니다.
데이터베이스 미사용, JSON 파일 저장 여부 검증:
모든 데이터 관련 테스트는 JsonDataHandler를 통해 임시 JSON 파일을 사용하며, 파일의 생성, 내용 변경, 로드를 확인합니다.