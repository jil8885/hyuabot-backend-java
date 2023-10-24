create schema if not exists public;

-- 관리자 계정 테이블 삭제
drop table if exists admin_user cascade;

-- 공지사항 테이블 삭제
drop table if exists notices cascade;
drop table if exists notice_category cascade;

-- 셔틀버스 테이블 삭제
drop table if exists shuttle_period cascade;
drop table if exists shuttle_timetable cascade;
drop table if exists shuttle_period_type cascade;
drop table if exists shuttle_route_stop cascade;
drop table if exists shuttle_route cascade;
drop table if exists shuttle_stop cascade;
drop table if exists shuttle_holiday cascade;

-- 통학버스 테이블 삭제
drop table if exists commute_shuttle_timetable cascade;
drop table if exists commute_shuttle_route cascade;
drop table if exists commute_shuttle_stop cascade;

-- 버스 테이블 삭제
drop table if exists bus_realtime cascade;
drop table if exists bus_route_stop cascade;
drop table if exists bus_timetable cascade;
drop table if exists bus_route cascade;
drop table if exists bus_stop cascade;

-- 전철 테이블 삭제
drop table if exists subway_realtime cascade;
drop table if exists subway_timetable cascade;
drop table if exists subway_route_station cascade;
drop table if exists subway_station cascade;
drop table if exists subway_route cascade;

-- 학식 테이블 삭제
drop table if exists menu cascade;
drop table if exists restaurant cascade;

-- 열람실 테이블 삭제
drop table if exists reading_room cascade;

-- 캠퍼스 테이블 삭제
drop table if exists campus cascade;

-- 관리자 계정 테이블
create table if not exists admin_user (
    user_id varchar(20) primary key,
    password varchar(100) not null,
    name varchar(20) not null,
    email varchar(50) not null,
    phone varchar(15) not null,
    active boolean not null
);

-- 공지사항 카테고리 테이블
create table if not exists notice_category (
    category_id int primary key,
    category_name varchar(20) not null
);


-- 공지사항 테이블
create table if not exists notices (
    notice_id int primary key,
    title varchar(100) not null,
    url varchar(200) not null,
    expired_at timestamptz,
    category_id int not null,
    user_id varchar(20) not null,
    constraint fk_category_id
        foreign key (category_id)
        references notice_category(category_id),
    constraint fk_user_id
        foreign key (user_id)
        references admin_user(user_id)
);

-- 셔틀버스 운행 기간 종류
create table if not exists shuttle_period_type (
    period_type varchar(20) primary key
);

-- 셔틀버스 정류장
create table if not exists shuttle_stop (
    stop_name varchar(15) primary key,
    latitude double precision,
    longitude double precision
);

-- 셔틀버스 운행 노선
create table if not exists shuttle_route (
    route_name varchar(15) primary key,
    route_description_korean varchar(100),
    route_description_english varchar(100),
    route_tag varchar(10),
    start_stop varchar(15) references shuttle_stop(stop_name),
    end_stop varchar(15) references shuttle_stop(stop_name)
);

-- 셔틀버스 노선별 정류장 순서
create table if not exists shuttle_route_stop (
    route_name varchar(15) references shuttle_route(route_name),
    stop_name varchar(15) references shuttle_stop(stop_name),
    stop_order int,
    cumulative_time interval not null,
    constraint pk_shuttle_route_stop primary key (route_name, stop_name)
);

-- 셔틀버스 운행 기간 (학기중, 계절학기, 방학)
create table if not exists shuttle_period(
    -- 셔틀버스 운행 기간 ID
    period_type varchar(20) not null,
    period_start timestamptz not null,
    period_end timestamptz not null,
    constraint pk_shuttle_period primary key (period_type, period_start, period_end),
    constraint fk_period_type
        foreign key (period_type)
        references shuttle_period_type(period_type)
);

-- 셔틀버스 운행 시간표
create table if not exists shuttle_timetable(
    seq serial primary key,
    period_type varchar(20) not null,
    weekday boolean not null, -- 평일 여부
    route_name varchar(15) not null,
    departure_time timetz not null,
    constraint fk_period_type
        foreign key (period_type)
        references shuttle_period_type(period_type),
    constraint fk_route_name_stop
        foreign key (route_name)
        references shuttle_route(route_name)
);

-- 셔틀 임시 휴일
create table if not exists shuttle_holiday(
    holiday_date date not null,
    holiday_type varchar(15) not null,
    calendar_type varchar(15) not null,
    constraint pk_shuttle_holiday primary key (holiday_date, holiday_type, calendar_type)
);

-- 셔틀 운행 시간표 뷰
create materialized view if not exists shuttle_timetable_view as
select
    shuttle_timetable.seq,
    shuttle_timetable.period_type,
    shuttle_timetable.weekday,
    shuttle_timetable.route_name,
    shuttle_route.route_tag,
    shuttle_route_stop.stop_name,
    shuttle_timetable.departure_time + shuttle_route_stop.cumulative_time as departure_time
from shuttle_timetable
inner join shuttle_period_type on shuttle_period_type.period_type = shuttle_timetable.period_type
inner join shuttle_route_stop on shuttle_route_stop.route_name = shuttle_timetable.route_name
inner join shuttle_route on shuttle_route_stop.route_name = shuttle_route.route_name
order by shuttle_timetable.seq, shuttle_route_stop.stop_order;

-- 셔틀 운행 시간표 뷰 업데이트 트리거
create or replace function update_shuttle_timetable_view()
returns trigger as $$
begin
    refresh materialized view shuttle_timetable_view;
    return new;
end;
$$ language plpgsql;

create trigger update_shuttle_timetable_view_timetable
after insert or update or delete on shuttle_timetable
for each row execute procedure update_shuttle_timetable_view();

create trigger update_shuttle_timetable_view_route_stop
after insert or update or delete on shuttle_route_stop
for each row execute procedure update_shuttle_timetable_view();

create trigger update_shuttle_timetable_view_route
after insert or update or delete on shuttle_route
for each row execute procedure update_shuttle_timetable_view();

-- 통학버스 운행 노선
create table if not exists commute_shuttle_route (
    route_name varchar(15) primary key,
    route_description_korean varchar(100),
    route_description_english varchar(100)
);

-- 통학버스 정류장
create table if not exists commute_shuttle_stop (
    stop_name varchar(50) primary key,
    description varchar(100),
    latitude double precision,
    longitude double precision
);

-- 통학버스 노선별 정류장 순서
create table if not exists commute_shuttle_timetable (
    route_name varchar(15) references commute_shuttle_route(route_name),
    stop_name varchar(50) references commute_shuttle_stop(stop_name),
    stop_order int,
    departure_time timetz not null,
    constraint pk_commute_shuttle_route_stop primary key (route_name, stop_name)
);

-- 버스 정류장
create table if not exists bus_stop (
    stop_id int primary key, -- 정류장 ID(GBIS)
    stop_name varchar(30), -- 정류장 이름
    district_code int not null, -- 인가기관 코드
    mobile_number varchar(15) not null, -- 정류장 검색 ID(숫자 5자리)
    region_name varchar(10) not null, -- 지역명
    latitude double precision not null, -- 정류장 위도
    longitude double precision not null -- 정류장 경도
);

-- 버스 노선
create table if not exists bus_route (
    -- 운행사 정보
    company_id int,
    company_name varchar(30) not null,
    company_telephone varchar(15) not null,
    -- 관리 기관 정보
    district_code int not null,
    -- 평일 기점 → 종점 방면 첫차, 막차
    up_first_time timetz not null,
    up_last_time timetz not null,
    -- 평일 종점 → 기점 방면 첫차, 막차
    down_first_time timetz not null,
    down_last_time timetz not null,
    -- 기점 정류소
    start_stop_id int not null,
    -- 종점 정류소
    end_stop_id int not null,
    -- 노선 정보
    route_id int primary key, -- 노선 ID(GBIS)
    route_name varchar(30) not null, -- 노선 이름
    route_type_code varchar(10) not null, -- 노선 유형
    route_type_name varchar(10) not null, -- 노선 유형 이름
    -- FK
    constraint fk_start_stop_id
        foreign key (start_stop_id)
        references bus_stop(stop_id),
    constraint fk_end_stop_id
        foreign key (end_stop_id)
        references bus_stop(stop_id)
);

-- 각 노선별 경유 정류장 목록 조회
create table if not exists bus_route_stop (
    route_id int not null,
    stop_id int not null,
    stop_sequence int not null,
    start_stop_id int not null,
    constraint pk_bus_route_stop primary key (route_id, stop_id),
    constraint fk_route_id
        foreign key (route_id)
        references bus_route(route_id),
    constraint fk_stop_id
        foreign key (stop_id)
        references bus_stop(stop_id),
    constraint fk_start_stop_id
        foreign key (start_stop_id)
        references bus_stop(stop_id)
);

-- 버스 실시간 운행 정보
create table if not exists bus_realtime(
    stop_id int not null, -- 정류장 ID
    route_id int not null, -- 노선 ID
    arrival_sequence int not null, -- 도착 순서
    remaining_stop_count int not null, -- 남은 정류장 수
    remaining_seat_count int not null, -- 남은 좌석 수
    remaining_time interval not null, -- 남은 시간
    low_plate boolean not null, -- 저상 버스 여부,
    last_updated_time timestamptz not null, -- 마지막 업데이트 시간
    constraint pk_bus_realtime primary key (stop_id, route_id, arrival_sequence),
    constraint fk_bus_realtime_stop_id
        foreign key (stop_id, route_id) references bus_route_stop(stop_id, route_id)
);

-- 버스 회차지 출발 시간표
create table if not exists bus_timetable(
    route_id int not null, -- 노선 ID
    start_stop_id int not null, -- 기점 정류장 ID
    departure_time timetz not null, -- 출발 시간
    weekday varchar(10) not null, -- 평일, 토요일, 일요일 여부
    constraint pk_bus_timetable primary key (route_id, start_stop_id, departure_time, weekday),
    constraint fk_route_id
        foreign key (route_id)
        references bus_route(route_id),
    constraint fk_start_stop_id
        foreign key (start_stop_id)
        references bus_stop(stop_id)
);

-- 전철역 정보
create table if not exists subway_station(
    station_name varchar(30) primary key -- 역 이름
);

-- 전철 노선 정보
create table if not exists subway_route(
    route_id int primary key, -- 노선 ID
    route_name varchar(30) not null -- 노선 이름
);

-- 전철 노선별 역 목록
create table if not exists subway_route_station(
    station_id varchar(10) primary key, -- 역 ID
    route_id int not null, -- 노선 ID
    station_name varchar(30) not null,-- 역 이름
    station_sequence int not null, -- 역 순서
    cumulative_time interval not null, -- 누적 시간
    constraint fk_route_id
        foreign key (route_id)
        references subway_route(route_id),
    constraint fk_station_id
        foreign key (station_name)
        references subway_station(station_name)
);

-- 전철 실시간 운행 정보
create table if not exists subway_realtime(
    station_id varchar(10) not null, -- 역 ID
    arrival_sequence int not null, -- 도착 순서
    current_station_name varchar(30) not null, -- 현재 역 이름
    remaining_stop_count int not null, -- 남은 정류장 수
    remaining_time interval not null, -- 남은 시간
    up_down_type varchar(10) not null, -- 상행, 하행 여부
    terminal_station_id varchar(10) not null, -- 종착역 ID
    train_number varchar(10) not null, -- 열차 번호
    last_updated_time timestamptz not null, -- 마지막 업데이트 시간
    is_express_train boolean not null, -- 급행 여부
    is_last_train boolean not null, -- 막차 여부
    status_code int not null, -- 상태 코드
    constraint pk_subway_realtime primary key (station_id, up_down_type, arrival_sequence),
    constraint fk_station_id
        foreign key (station_id)
        references subway_route_station(station_id),
    constraint fk_terminal_station_id
        foreign key (terminal_station_id)
        references subway_route_station(station_id)
);

-- 전철 시간표
create table if not exists subway_timetable(
    station_id varchar(10) not null, -- 역 ID
    start_station_id varchar(10) not null, -- 출발역 ID
    terminal_station_id varchar(10) not null, -- 종착역 ID
    departure_time timetz not null, -- 출발 시간
    weekday varchar(10) not null, -- 평일, 토요일, 일요일 여부
    up_down_type varchar(10) not null, -- 상행, 하행 여부
    constraint pk_subway_timetable primary key (station_id, up_down_type, weekday, departure_time),
    constraint fk_station_id
        foreign key (station_id)
        references subway_route_station(station_id),
    constraint fk_start_station_id
        foreign key (start_station_id)
        references subway_route_station(station_id),
    constraint fk_terminal_station_id
        foreign key (terminal_station_id)
        references subway_route_station(station_id)
);

-- 캠퍼스
create table if not exists campus(
    campus_id int primary key, -- 캠퍼스 ID
    campus_name varchar(30) not null -- 캠퍼스 이름
);

-- 학식을 제공하는 식당
create table if not exists restaurant(
    campus_id int not null, -- 캠퍼스 ID
    restaurant_id int primary key, -- 식당 ID
    restaurant_name varchar(50) not null, -- 식당 이름
    latitude double precision not null, -- 식당 위도
    longitude double precision not null, -- 식당 경도
    constraint fk_campus_id
        foreign key (campus_id)
        references campus(campus_id)
);

-- 학식 메뉴
create table if not exists menu(
    restaurant_id int not null, -- 식당 ID
    feed_date date not null, -- 급식 날짜,
    time_type varchar(10) not null, -- 시간 타입 (아침, 점심, 저녁)
    menu_food varchar(400) not null, -- 메뉴 이름
    menu_price varchar(30) not null, -- 메뉴 가격
    constraint pk_menu primary key (restaurant_id, feed_date, time_type, menu_food),
    constraint fk_restaurant_id
        foreign key (restaurant_id)
        references restaurant(restaurant_id)
);

-- 열람실 정보
create table if not exists reading_room(
    campus_id int not null, -- 캠퍼스 ID
    room_id int primary key, -- 열람실 ID
    room_name varchar(30) not null, -- 열람실 이름
    is_active boolean not null, -- 열람실 활성화 여부
    is_reservable boolean not null, -- 열람실 예약 가능 여부
    total int not null, -- 열람실 총 좌석 수
    active_total int not null, -- 열람실 활성화된 좌석 수
    occupied int not null, -- 열람실 사용중인 좌석 수
    available int generated always as ( active_total - occupied ) stored , -- 열람실 사용 가능한 좌석 수
    last_updated_time timestamptz, -- 마지막 업데이트 시간
    constraint fk_campus_id
        foreign key (campus_id)
        references campus(campus_id)
);