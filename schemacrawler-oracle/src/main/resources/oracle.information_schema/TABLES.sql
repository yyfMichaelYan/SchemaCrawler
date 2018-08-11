SELECT  
  NULL AS TABLE_CAT,
  TABLES.OWNER AS TABLE_SCHEM,
  TABLES.TABLE_NAME AS TABLE_NAME,
    TABLES.TABLE_TYPE AS TABLE_TYPE,
    TABLES.COMMENTS AS REMARKS
FROM 
  DBA_TAB_COMMENTS TABLES
WHERE
  TABLES.OWNER NOT IN 
    ('ANONYMOUS', 'APEX_PUBLIC_USER', 'APPQOSSYS', 'BI', 'CTXSYS', 'DBSNMP', 'DIP', 
    'EXFSYS', 'FLOWS_30000', 'FLOWS_FILES', 'GSMADMIN_INTERNAL', 'IX', 'LBACSYS', 
    'MDDATA', 'MDSYS', 'MGMT_VIEW', 'OE', 'OLAPSYS', 'ORACLE_OCM', 
    'ORDPLUGINS', 'ORDSYS', 'OUTLN', 'OWBSYS', 'PM', 'SCOTT', 'SH', 
    'SI_INFORMTN_SCHEMA', 'SPATIAL_CSW_ADMIN_USR', 'SPATIAL_WFS_ADMIN_USR', 
    'SYS', 'SYSMAN', 'SYSTEM', 'TSMSYS', 'WKPROXY', 'WKSYS', 'WK_TEST', 
    'WMSYS', 'XDB', 'XS$NULL', 'RDSADMIN')  
  AND NOT REGEXP_LIKE(TABLES.OWNER, '^APEX_[0-9]{6}$')
  AND NOT REGEXP_LIKE(TABLES.OWNER, '^FLOWS_[0-9]{5,6}$')
  AND TABLES.TABLE_NAME NOT LIKE 'BIN$%'
  AND NOT REGEXP_LIKE(TABLES.TABLE_NAME, '^(SYS_IOT|MDOS|MDRS|MDRT|MDOT|MDXT)_.*$')
ORDER BY 
  TABLE_SCHEM, 
  TABLE_NAME