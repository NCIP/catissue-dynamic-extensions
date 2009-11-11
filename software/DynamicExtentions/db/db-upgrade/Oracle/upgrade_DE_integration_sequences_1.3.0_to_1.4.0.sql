declare
    i number :=1; j number :=1;
    sql_stmt varchar2(150);
begin
	select nvl(max(identifier),0)+1 into i from DYEXTN_ABSTRACT_FORM_CONTEXT;
    sql_stmt :='create sequence DYEXTN_ABSTRACT_FRM_CTXT_SEQ start with ' || i ;
    execute immediate sql_stmt;

	select nvl(max(identifier),0)+1 into j from DYEXTN_ABSTRACT_RECORD_ENTRY;
    sql_stmt :='create sequence DYEXTN_ABSTRACT_RE_SEQ start with ' || j ;
    execute immediate sql_stmt;
end;