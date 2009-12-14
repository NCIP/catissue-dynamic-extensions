declare
    i number :=1;
    sql_stmt varchar2(150);
begin
    sql_stmt :='drop sequence DYEXTN_ABSTRACT_RE_SEQ';
    execute immediate sql_stmt;

	select nvl(max(identifier),0)+1 into i from DYEXTN_ABSTRACT_RECORD_ENTRY;
    sql_stmt :='create sequence DYEXTN_ABSTRACT_RE_SEQ start with ' || i ;
    execute immediate sql_stmt;
end;