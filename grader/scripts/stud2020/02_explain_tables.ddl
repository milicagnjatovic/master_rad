----------------------------------------------------------------------------
connect to stud2020;

call sysproc.sysinstallobjects('EXPLAIN', 'C',
cast (null as varchar(128)), cast (null as varchar(128)));

grant all
on table SYSTOOLS.ADVISE_INDEX
to public;

grant all
on table SYSTOOLS.ADVISE_INSTANCE
to public;

grant all
on table SYSTOOLS.ADVISE_MQT
to public;

grant all
on table SYSTOOLS.ADVISE_PARTITION
to public;

grant all
on table SYSTOOLS.ADVISE_TABLE
to public;

grant all
on table SYSTOOLS.ADVISE_WORKLOAD
to public;

grant all
on table SYSTOOLS.EXPLAIN_ACTUALS
to public;

grant all
on table SYSTOOLS.EXPLAIN_ARGUMENT
to public;

grant all
on table SYSTOOLS.EXPLAIN_DIAGNOSTIC
to public;

grant all
on table SYSTOOLS.EXPLAIN_DIAGNOSTIC_DATA
to public;

grant all
on table SYSTOOLS.EXPLAIN_INSTANCE
to public;

grant all
on table SYSTOOLS.EXPLAIN_OBJECT
to public;

grant all
on table SYSTOOLS.EXPLAIN_OPERATOR
to public;

grant all
on table SYSTOOLS.EXPLAIN_PREDICATE
to public;

grant all
on table SYSTOOLS.EXPLAIN_STATEMENT
to public;

grant all
on table SYSTOOLS.EXPLAIN_STREAM
to public;

grant all
on table SYSTOOLS.OBJECT_METRICS
to public;

connect reset;
----------------------------------------------------------------------------
