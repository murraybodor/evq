--
-- EVQ application DDL
--

-- Create synonyms to DWH objects 
-- CMEUL_RO role must be set up!
create or replace synonym IHFC_CORP_HIST_LTLF_V
  for CMEUL.IHFC_CORP_HIST_LTLF_V@DWHP.AESO;

create or replace synonym IHFC_CORP_HIST_BUS_V
  for CMEUL.IHFC_CORP_HIST_BUS_V@DWHP.AESO; 

create or replace synonym IHFC_MEASURE_POINT_V
  for CMEUL.IHFC_MEASURE_POINT_V@DWHP.AESO;
  
create or replace synonym IHFC_MEASURE_POINT_TYPE_V
  for CMEUL.IHFC_MEASURE_POINT_TYPE_V@DWHP.AESO;

create or replace synonym IHFC_BUS_V
  for CMEUL.IHFC_BUS_V@DWHP.AESO;

create or replace synonym IHFC_FACILITY_V
  for CMEUL.IHFC_FACILITY_V@DWHP.AESO; 

create or replace synonym IHFC_SYS_AREAS_V
  for CMEUL.IHFC_SYS_AREAS_V@DWHP.AESO;

create or replace synonym IHFC_REGIONS_V
  for CMEUL.IHFC_REGIONS_V@DWHP.AESO;

create or replace synonym IHFC_REGIONS_AS_AREAS_V
  for CMEUL.IHFC_REGIONS_AS_AREAS_V@DWHP.AESO;

-- Create sequence
DROP SEQUENCE ELEC_VOLUMES_QUERY_SEQ;

CREATE SEQUENCE ELEC_VOLUMES_QUERY_SEQ
	INCREMENT BY 1
	START WITH 1;


-- Create table
drop table ELEC_VOLUMES_QUERY_PARM_T;
drop table ELEC_VOLUMES_QUERY_RESULT_T;
drop table ELEC_VOLUMES_QUERY_T;

create table ELEC_VOLUMES_QUERY_T
(
  QUERY_ID				NUMBER NOT NULL,
  USER_ID				VARCHAR2(50) not null,
  QUERY_NAME 			VARCHAR2(400),
  DATE_QUERY_TYPE		VARCHAR2(2) not null, -- (SP)ecific, (CA)lendar, (TW)o season or (FO)ur season
  BEGIN_DATE			DATE,
  END_DATE				DATE,
  CAL_BEGIN_YEAR		INTEGER,
  CAL_BEGIN_MONTH		INTEGER,
  CAL_END_YEAR			INTEGER,
  CAL_END_MONTH			INTEGER,
  TS_BEGIN_YEAR			INTEGER,
  TS_END_YEAR			INTEGER,
  TS_SUMMER_FLAG		CHAR(1), -- Y or N
  TS_WINTER_FLAG		CHAR(1), -- Y or N
  FS_BEGIN_YEAR			INTEGER,
  FS_END_YEAR			INTEGER,
  FS_SPRING_FLAG		CHAR(1), -- Y or N
  FS_SUMMER_FLAG		CHAR(1), -- Y or N
  FS_FALL_FLAG			CHAR(1), -- Y or N
  FS_WINTER_FLAG		CHAR(1), -- Y or N
  GEOG_QUERY_TYPE       VARCHAR2(2), -- ES, RG, PA, SB, MP, IE
  GRANULARITY           VARCHAR2(2), -- see CODES and GRAN tables
  CATEGORY              VARCHAR2(2), -- RM only
  LOAD_FLAG				CHAR(1), -- Y or N
  GENERATION_FLAG		CHAR(1), -- Y or N
  POI_QUERY_TYPE	    VARCHAR2(2), -- (PE)rcentile or (HO)urly
  POI_COINCIDENCE       VARCHAR2(2), -- see CODES table
  POI_CATEGORY 			VARCHAR2(2), -- RM only  
  POI_LOAD_FLAG         CHAR(1), -- Y or N
  POI_GENERATION_FLAG   CHAR(1), -- Y or N
  TIME_INTERVAL			VARCHAR2(2), -- see CODES table
  POI_PEAK_FLAG   		CHAR(1), -- Y or N
  POI_MEDIAN_FLAG  		CHAR(1), -- Y or N
  POI_LIGHT_FLAG   		CHAR(1), -- Y or N
  QUERY_SQL_STRING      VARCHAR2(4000),
  AUDIT_DATETIME		DATE not null
)
tablespace MED_CORP_DATA;

comment on table ELEC_VOLUMES_QUERY_T 					is 'Holds electrical volume queries'; 
comment on column ELEC_VOLUMES_QUERY_T.QUERY_ID 		is 'Unique query identifier';
comment on column ELEC_VOLUMES_QUERY_T.QUERY_NAME 		is 'User-defined query name (optional)';


-- Create/Recreate primary key and indexes
alter table ELEC_VOLUMES_QUERY_T
  add constraint ELEC_VOLUMES_QUERY_PK primary key (QUERY_ID)
  using index
  tablespace MED_CORP_INDX;

create table ELEC_VOLUMES_QUERY_PARM_T
(
  QUERY_ID				NUMBER NOT NULL,
  PARAMETER_ID			NUMBER NOT NULL,
  PARAMETER_TYPE		VARCHAR2(2) not null,
  PARAMETER_VALUE		VARCHAR2(30)
)
tablespace MED_CORP_DATA;

comment on table ELEC_VOLUMES_QUERY_PARM_T 					is 'Holds electrical volume query parameters'; 

ALTER TABLE ELEC_VOLUMES_QUERY_PARM_T
  add CONSTRAINT ELEC_VOLUMES_QUERY_PARM_PK primary key (QUERY_ID, PARAMETER_ID)
  using index
  tablespace MED_CORP_INDX;

ALTER TABLE ELEC_VOLUMES_QUERY_PARM_T
add CONSTRAINT evqp_fk1
  FOREIGN KEY (query_id)
  REFERENCES ELEC_VOLUMES_QUERY_T(query_id);

create table ELEC_VOLUMES_QUERY_RESULT_T
(
  QUERY_ID				NUMBER NOT NULL,
  QUERY_STATUS          VARCHAR2(1) NOT NULL,
  QUERY_RESULT          BLOB DEFAULT EMPTY_BLOB() NOT NULL,
  QUERY_RUNTIME_MS		NUMBER,
  QUERY_ERRORS			VARCHAR2(4000)
)
tablespace MED_CORP_DATA;

comment on table ELEC_VOLUMES_QUERY_RESULT_T 					is 'Holds electrical volume query results'; 

ALTER TABLE ELEC_VOLUMES_QUERY_RESULT_T
  add CONSTRAINT ELEC_VOLUMES_QUERY_RESULT_PK primary key (QUERY_ID)
  using index
  tablespace MED_CORP_INDX;

ALTER TABLE ELEC_VOLUMES_QUERY_RESULT_T
add CONSTRAINT evqr_fk1
  FOREIGN KEY (query_id)
  REFERENCES ELEC_VOLUMES_QUERY_T(query_id);

drop table ELEC_VOLUMES_CODES_T;
create table ELEC_VOLUMES_CODES_T
(
  CODE_TYPE             VARCHAR2(8) NOT NULL,
  CODE_VALUE			VARCHAR2(8) NOT NULL,
  CODE_DESC	        	VARCHAR2(30) NOT NULL,
  ORDER_INFO		    NUMBER, 
  SUPP_INFO             VARCHAR2(30)
)
tablespace MED_CORP_DATA;

comment on table ELEC_VOLUMES_CODES_T 					is 'Holds code values used by the EVQ application'; 


INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'IL', 'AIL', 1, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'IS', 'AIES', 2, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'DE', 'Total of Demand', 3, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'SU', 'Total of Supply', 4, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'ES', 'Entire System', 5, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'RG', 'Region', 6, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'PA', 'Planning Area', 7, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'SB', 'Substation', 8, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'MP', 'Meas. Point', 10, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'BU', 'Bus', 11, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'IE', 'Imports/Exports', 12, null);

INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('CAT', 'RM', 'Revenue Metered', 13, null);

INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('INTV', 'TO', 'Total', 14, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('INTV', 'YR', 'Yearly', 15, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('INTV', 'SE', 'Seasonal', 16, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('INTV', 'MO', 'Monthly', 17, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('INTV', 'DA', 'Daily', 19, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('INTV', 'HR', 'Hourly', 20, null);

INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('DATE', 'SP', 'Specific', 21, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('DATE', 'CA', 'Calendar', 22, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('DATE', 'TW', 'Two Season', 23, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('DATE', 'FO', 'Four Season', 24, null);

INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('POI', 'PE', 'Percentile', 25, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('POI', 'HO', 'Hourly', 26, null);

INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'TA', 'Total of Areas', 30, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'TS', 'Total of Subs', 35, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('GEOG', 'TM', 'Total of MPs', 40, null);

INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('REAP', '7', 'Blob expiry', 45, null);
INSERT INTO ELEC_VOLUMES_CODES_T VALUES ('RESET', '1', 'Query status reset', 50, null);


drop table ELEC_VOLUMES_COINCIDENCE_T;
create table ELEC_VOLUMES_COINCIDENCE_T
(
  GEOG_VALUE			VARCHAR2(8) NOT NULL,
  GRAN_VALUE			VARCHAR2(8) NOT NULL,
  COINC_VALUE			VARCHAR2(8) NOT NULL
)
tablespace MED_CORP_DATA;

comment on table ELEC_VOLUMES_COINCIDENCE_T 					is 'Holds coincidence values used by the EVQ application'; 


-- Entire System
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'IL', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'IL', 'SU');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'IS', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'IS', 'SU');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'DE', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'DE', 'SU');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'SU', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'SU', 'SU');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'RG', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'RG', 'SU');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'PA', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'PA', 'SU');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'MP', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'MP', 'SU');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'SB', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'SB', 'SU');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'BU', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('ES', 'BU', 'SU');

-- Region
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'RG', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'RG', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'RG', 'RG');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'PA', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'PA', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'PA', 'RG');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'PA', 'PA');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'MP', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'MP', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'MP', 'RG');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'MP', 'PA');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'MP', 'MP');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'SB', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'SB', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'SB', 'RG');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'SB', 'PA');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'SB', 'SB');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'BU', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'BU', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'BU', 'RG');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'BU', 'PA');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('RG', 'BU', 'SB');

-- Planning area
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'PA', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'PA', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'PA', 'PA');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'TA', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'TA', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'TA', 'TA');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'MP', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'MP', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'MP', 'PA');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'MP', 'MP');

-- no hourly
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'SB', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'SB', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'SB', 'PA');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'SB', 'SB');

-- no hourly
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'BU', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'BU', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'BU', 'PA');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('PA', 'BU', 'SB');

-- Meas Point
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('MP', 'MP', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('MP', 'MP', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('MP', 'MP', 'MP');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('MP', 'MP', 'TM');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('MP', 'MP', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('MP', 'MP', 'SU');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('MP', 'TM', 'TM');

-- Substation
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('SB', 'SB', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('SB', 'SB', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('SB', 'SB', 'SB');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('SB', 'SB', 'TS');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('SB', 'TS', 'TS');

INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('SB', 'BU', 'DE');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('SB', 'BU', 'SU');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('SB', 'BU', 'SB');
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('SB', 'BU', 'TS');

-- Import/export
INSERT INTO ELEC_VOLUMES_COINCIDENCE_T VALUES ('IE', 'IE', 'IE');


/* 
 * Common queries 
 */ 
/* queries */
insert into elec_volumes_query_t (QUERY_ID, USER_ID, QUERY_NAME, DATE_QUERY_TYPE, BEGIN_DATE, END_DATE, CAL_BEGIN_YEAR, CAL_BEGIN_MONTH, CAL_END_YEAR, CAL_END_MONTH, TS_BEGIN_YEAR, TS_END_YEAR, TS_SUMMER_FLAG, TS_WINTER_FLAG, FS_BEGIN_YEAR, FS_END_YEAR, FS_SPRING_FLAG, FS_SUMMER_FLAG, FS_FALL_FLAG, FS_WINTER_FLAG, GEOG_QUERY_TYPE, GRANULARITY, CATEGORY, LOAD_FLAG, GENERATION_FLAG, POI_QUERY_TYPE, POI_COINCIDENCE, POI_CATEGORY, POI_LOAD_FLAG, POI_GENERATION_FLAG, TIME_INTERVAL, POI_PEAK_FLAG, POI_MEDIAN_FLAG, POI_LIGHT_FLAG, QUERY_SQL_STRING, AUDIT_DATETIME)
values (ELEC_VOLUMES_QUERY_SEQ.NEXTVAL, 'COMMON', 'Substation Load at time of Substation Peak', 'TW', null, null, null, null, null, null, 2003, 2007, 'Y', 'Y', null, null, 'N', 'N', 'N', 'N', 'PA', 'SB', 'RM', 'Y', 'N', 'PE', 'SB', 'RM', 'Y', 'N', 'SE', 'Y', 'N', 'N', 'SELECT t2000.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME as TWO_SEASON_NAME , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t100.TWO_SEASON_NAME as TWO_SEASON_NAME , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.AREA_CODE as AREA_CODE , t100.FACILITY_CODE as FACILITY_CODE , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t10.TWO_SEASON_NAME as TWO_SEASON_NAME , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.AREA_CODE as AREA_CODE , t10.FACILITY_CODE as FACILITY_CODE , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || ''-'' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.TWO_SEASON_YEAR,  t10.TWO_SEASON_NAME,  t10.AREA_CODE,  t10.FACILITY_CODE,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, ''Peak'' ) as POINTOFINTEREST FROM ( SELECT t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , t1.FACILITY_CODE as FACILITY_CODE , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.AREA_CODE,  t1.FACILITY_CODE) as MAX_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = ''DEM'' AND t1.incl_in_pod_lsb = ''Y'' AND  t1.two_season_year BETWEEN 2003 AND 2007 AND  t1.area_code IN (''30'', ''34'', ''38'', ''44'') GROUP BY t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE , t1.FACILITY_CODE ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || ''-'' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t1000.facility_code = t2000.facility_code AND t2000.measurement_point_type_code IN (''DEM'' )  AND t2000.incl_in_pod_lsb = ''Y'' AND  t2000.area_code IN (''30'', ''34'', ''38'', ''44'') AND  t2000.two_season_year BETWEEN 2003 AND 2007 GROUP BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST', sysdate);

insert into elec_volumes_query_t (QUERY_ID, USER_ID, QUERY_NAME, DATE_QUERY_TYPE, BEGIN_DATE, END_DATE, CAL_BEGIN_YEAR, CAL_BEGIN_MONTH, CAL_END_YEAR, CAL_END_MONTH, TS_BEGIN_YEAR, TS_END_YEAR, TS_SUMMER_FLAG, TS_WINTER_FLAG, FS_BEGIN_YEAR, FS_END_YEAR, FS_SPRING_FLAG, FS_SUMMER_FLAG, FS_FALL_FLAG, FS_WINTER_FLAG, GEOG_QUERY_TYPE, GRANULARITY, CATEGORY, LOAD_FLAG, GENERATION_FLAG, POI_QUERY_TYPE, POI_COINCIDENCE, POI_CATEGORY, POI_LOAD_FLAG, POI_GENERATION_FLAG, TIME_INTERVAL, POI_PEAK_FLAG, POI_MEDIAN_FLAG, POI_LIGHT_FLAG, QUERY_SQL_STRING, AUDIT_DATETIME)
values (ELEC_VOLUMES_QUERY_SEQ.NEXTVAL, 'COMMON', 'Substation Load at time of Planning Area Peak', 'TW', null, null, null, null, null, null, 2003, 2007, 'Y', 'Y', null, null, 'N', 'N', 'N', 'N', 'PA', 'SB', 'RM', 'Y', 'N', 'PE', 'PA', 'RM', 'Y', 'N', 'SE', 'Y', 'N', 'N', 'SELECT t2000.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME as TWO_SEASON_NAME , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t100.TWO_SEASON_NAME as TWO_SEASON_NAME , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.AREA_CODE as AREA_CODE , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t10.TWO_SEASON_NAME as TWO_SEASON_NAME , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.AREA_CODE as AREA_CODE , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || ''-'' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.TWO_SEASON_YEAR,  t10.TWO_SEASON_NAME,  t10.AREA_CODE,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, ''Peak'' ) as POINTOFINTEREST FROM ( SELECT t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t1.AREA_CODE as AREA_CODE , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME,  t1.AREA_CODE) as MAX_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = ''DEM'' AND t1.incl_in_pod_lsb = ''Y'' AND  t1.two_season_year BETWEEN 2003 AND 2007 AND  t1.area_code IN (''30'', ''34'', ''38'', ''44'') GROUP BY t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING , t1.AREA_CODE ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || ''-'' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t1000.area_code = t2000.area_code AND t2000.measurement_point_type_code IN (''DEM'' )  AND t2000.incl_in_pod_lsb = ''Y'' AND  t2000.area_code IN (''30'', ''34'', ''38'', ''44'') AND  t2000.two_season_year BETWEEN 2003 AND 2007 GROUP BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST', sysdate);

insert into elec_volumes_query_t (QUERY_ID, USER_ID, QUERY_NAME, DATE_QUERY_TYPE, BEGIN_DATE, END_DATE, CAL_BEGIN_YEAR, CAL_BEGIN_MONTH, CAL_END_YEAR, CAL_END_MONTH, TS_BEGIN_YEAR, TS_END_YEAR, TS_SUMMER_FLAG, TS_WINTER_FLAG, FS_BEGIN_YEAR, FS_END_YEAR, FS_SPRING_FLAG, FS_SUMMER_FLAG, FS_FALL_FLAG, FS_WINTER_FLAG, GEOG_QUERY_TYPE, GRANULARITY, CATEGORY, LOAD_FLAG, GENERATION_FLAG, POI_QUERY_TYPE, POI_COINCIDENCE, POI_CATEGORY, POI_LOAD_FLAG, POI_GENERATION_FLAG, TIME_INTERVAL, POI_PEAK_FLAG, POI_MEDIAN_FLAG, POI_LIGHT_FLAG, QUERY_SQL_STRING, AUDIT_DATETIME)
values (ELEC_VOLUMES_QUERY_SEQ.NEXTVAL, 'COMMON', 'Substation Load at time of Total Demand Peak', 'TW', null, null, null, null, null, null, 2003, 2007, 'Y', 'Y', null, null, 'N', 'N', 'N', 'N', 'PA', 'SB', 'RM', 'Y', 'N', 'PE', 'DE', 'RM', 'Y', 'N', 'SE', 'Y', 'N', 'N', 'SELECT t2000.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME as TWO_SEASON_NAME , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t100.TWO_SEASON_NAME as TWO_SEASON_NAME , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t10.TWO_SEASON_NAME as TWO_SEASON_NAME , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || ''-'' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.TWO_SEASON_YEAR,  t10.TWO_SEASON_NAME,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, ''Peak'' ) as POINTOFINTEREST FROM ( SELECT t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME) as MAX_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_LTLF_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = ''DEM'' AND t1.incl_in_pod_lsb = ''Y'' AND  t1.two_season_year BETWEEN 2003 AND 2007 GROUP BY t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || ''-'' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN (''DEM'' )  AND t2000.incl_in_pod_lsb = ''Y'' AND  t2000.area_code IN (''30'', ''34'', ''38'', ''44'') AND  t2000.two_season_year BETWEEN 2003 AND 2007 GROUP BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST', sysdate);

insert into elec_volumes_query_t (QUERY_ID, USER_ID, QUERY_NAME, DATE_QUERY_TYPE, BEGIN_DATE, END_DATE, CAL_BEGIN_YEAR, CAL_BEGIN_MONTH, CAL_END_YEAR, CAL_END_MONTH, TS_BEGIN_YEAR, TS_END_YEAR, TS_SUMMER_FLAG, TS_WINTER_FLAG, FS_BEGIN_YEAR, FS_END_YEAR, FS_SPRING_FLAG, FS_SUMMER_FLAG, FS_FALL_FLAG, FS_WINTER_FLAG, GEOG_QUERY_TYPE, GRANULARITY, CATEGORY, LOAD_FLAG, GENERATION_FLAG, POI_QUERY_TYPE, POI_COINCIDENCE, POI_CATEGORY, POI_LOAD_FLAG, POI_GENERATION_FLAG, TIME_INTERVAL, POI_PEAK_FLAG, POI_MEDIAN_FLAG, POI_LIGHT_FLAG, QUERY_SQL_STRING, AUDIT_DATETIME)
values (ELEC_VOLUMES_QUERY_SEQ.NEXTVAL, 'COMMON', 'Substation Load at time of All Substation Peak', 'TW', null, null, null, null, null, null, 2003, 2007, 'Y', 'Y', null, null, 'N', 'N', 'N', 'N', 'SB', 'SB', 'RM', 'Y', 'N', 'PE', 'TS', 'RM', 'Y', 'N', 'SE', 'Y', 'N', 'N', 'SELECT t2000.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME as TWO_SEASON_NAME , t2000.CAL_DAY_DATE as CAL_DAY_DATE , t2000.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t2000.AREA_CODE as AREA_CODE , t2000.FACILITY_CODE as FACILITY_CODE , t2000.SUBSTATION_NAME as SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE as MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST as POINTOFINTEREST , SUM(t2000.load_mw) as MW , SUM(t2000.load_mvar) as MVAR FROM ( SELECT t100.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t100.TWO_SEASON_NAME as TWO_SEASON_NAME , t100.CAL_DAY_DATE as CAL_DAY_DATE , t100.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t100.POINTOFINTEREST as POINTOFINTEREST FROM ( SELECT t10.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t10.TWO_SEASON_NAME as TWO_SEASON_NAME , t10.CAL_DAY_DATE as CAL_DAY_DATE , t10.CAL_HOUR_ENDING as CAL_HOUR_ENDING , t10.SUM_LOAD_MW as SUM_LOAD_MW , MIN (t10.CAL_DAY_DATE || ''-'' || t10.CAL_HOUR_ENDING) over( PARTITION BY  t10.TWO_SEASON_YEAR,  t10.TWO_SEASON_NAME,  t10.SUM_LOAD_MW) as POI_date_HE , DECODE (sum_load_mw, max_sum_load_mw, ''Peak'' ) as POINTOFINTEREST FROM ( SELECT t1.TWO_SEASON_YEAR as TWO_SEASON_YEAR , t1.TWO_SEASON_NAME as TWO_SEASON_NAME , t1.CAL_DAY_DATE as CAL_DAY_DATE , t1.CAL_HOUR_ENDING as CAL_HOUR_ENDING , SUM(t1.load_mw) as SUM_LOAD_MW , MAX (SUM(t1.load_mw)) over( PARTITION BY  t1.TWO_SEASON_YEAR,  t1.TWO_SEASON_NAME) as MAX_SUM_LOAD_MW FROM ( IHFC_CORP_HIST_BUS_V ) t1 WHERE 1=1  AND t1.measurement_point_type_code = ''DEM'' AND t1.incl_in_pod_lsb = ''Y'' AND  t1.two_season_year BETWEEN 2003 AND 2007 AND  t1.facility_code IN (''131S'', ''178S'', ''249S'', ''283S'', ''294S'', ''3080S'', ''358S'', ''359S'', ''384S'', ''445S'', ''454S'', ''477S'', ''489S'', ''61S'', ''62S'', ''68S'', ''250P'', ''115S'', ''118S'', ''123S'', ''20S'', ''245S'', ''271S'', ''272S'', ''291S'', ''29S'', ''2S'', ''32S'', ''33S'', ''35S'', ''3S'', ''437S'', ''43S'', ''44S'', ''48S'', ''945S'', ''953S'', ''985S'', ''T793S'') GROUP BY t1.TWO_SEASON_YEAR , t1.TWO_SEASON_NAME , t1.CAL_DAY_DATE , t1.CAL_HOUR_ENDING ) t10 WHERE 1=1  AND t10.sum_load_mw IN ( max_sum_load_mw ) ) t100 WHERE 1=1  AND t100.CAL_DAY_DATE || ''-'' || t100.CAL_HOUR_ENDING = t100.POI_date_HE  ) t1000 , IHFC_CORP_HIST_BUS_V t2000 WHERE 1=1  AND t1000.cal_day_date = t2000.cal_day_date AND t1000.cal_hour_ending = t2000.cal_hour_ending AND t2000.measurement_point_type_code IN (''DEM'' )  AND t2000.incl_in_pod_lsb = ''Y'' AND  t2000.facility_code IN (''131S'', ''178S'', ''249S'', ''283S'', ''294S'', ''3080S'', ''358S'', ''359S'', ''384S'', ''445S'', ''454S'', ''477S'', ''489S'', ''61S'', ''62S'', ''68S'', ''250P'', ''115S'', ''118S'', ''123S'', ''20S'', ''245S'', ''271S'', ''272S'', ''291S'', ''29S'', ''2S'', ''32S'', ''33S'', ''35S'', ''3S'', ''437S'', ''43S'', ''44S'', ''48S'', ''945S'', ''953S'', ''985S'', ''T793S'') AND  t2000.two_season_year BETWEEN 2003 AND 2007 GROUP BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.CAL_DAY_DATE , t2000.CAL_HOUR_ENDING , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST ORDER BY t2000.TWO_SEASON_YEAR , t2000.TWO_SEASON_NAME , t2000.AREA_CODE , t2000.FACILITY_CODE , t2000.SUBSTATION_NAME , t2000.MEASUREMENT_POINT_TYPE_CODE , t1000.POINTOFINTEREST', sysdate);

/* results */
insert into elec_volumes_query_result_t (QUERY_ID, QUERY_STATUS, QUERY_RESULT, QUERY_RUNTIME_MS, QUERY_ERRORS)
values (1, 'R', EMPTY_BLOB(), 2940483, 'Complete');

insert into elec_volumes_query_result_t (QUERY_ID, QUERY_STATUS, QUERY_RESULT, QUERY_RUNTIME_MS, QUERY_ERRORS)
values (2, 'R', EMPTY_BLOB(), 165378, 'Complete');

insert into elec_volumes_query_result_t (QUERY_ID, QUERY_STATUS, QUERY_RESULT, QUERY_RUNTIME_MS, QUERY_ERRORS)
values (3, 'R', EMPTY_BLOB(), 810837, 'Complete');

insert into elec_volumes_query_result_t (QUERY_ID, QUERY_STATUS, QUERY_RESULT, QUERY_RUNTIME_MS, QUERY_ERRORS)
values (4, 'R', EMPTY_BLOB(), 3374268, 'Complete');

/* parameters */
insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (1, 1, 'PA', '30');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (1, 2, 'PA', '34');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (1, 3, 'PA', '38');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (1, 4, 'PA', '44');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (2, 1, 'PA', '30');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (2, 2, 'PA', '34');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (2, 3, 'PA', '38');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (2, 4, 'PA', '44');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (3, 1, 'PA', '30');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (3, 2, 'PA', '34');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (3, 3, 'PA', '38');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (3, 4, 'PA', '44');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 1, 'SB', '131S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 2, 'SB', '178S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 3, 'SB', '249S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 4, 'SB', '283S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 5, 'SB', '294S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 6, 'SB', '3080S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 7, 'SB', '358S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 8, 'SB', '359S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 9, 'SB', '384S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 10, 'SB', '445S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 11, 'SB', '454S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 12, 'SB', '477S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 13, 'SB', '489S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 14, 'SB', '61S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 15, 'SB', '62S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 16, 'SB', '68S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 17, 'SB', '250P');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 18, 'SB', '115S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 19, 'SB', '118S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 20, 'SB', '123S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 21, 'SB', '20S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 22, 'SB', '245S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 23, 'SB', '271S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 24, 'SB', '272S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 25, 'SB', '291S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 26, 'SB', '29S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 27, 'SB', '2S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 28, 'SB', '32S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 29, 'SB', '33S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 30, 'SB', '35S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 31, 'SB', '3S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 32, 'SB', '437S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 33, 'SB', '43S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 34, 'SB', '44S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 35, 'SB', '48S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 36, 'SB', '945S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 37, 'SB', '953S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 38, 'SB', '985S');

insert into elec_volumes_query_parm_t (QUERY_ID, PARAMETER_ID, PARAMETER_TYPE, PARAMETER_VALUE)
values (4, 39, 'SB', 'T793S');


commit;

