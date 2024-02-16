revoke alterin on schema da from public by all;
revoke createin on schema da from public by all;
revoke dropin on schema da from public by all;
-----------------------------------------------------------
create schema student;
revoke alterin on schema student from public by all;
revoke createin on schema student from public by all;
revoke dropin on schema student from public by all;
----------------------------------------------------------------------------
grant select
on table da.NivoKvalifikacije
to public;

grant select
on table da.StudijskiProgram
to public;

grant select
on table da.Predmet
to public;

grant select
on table da.PredmetPrograma
to public;

grant select
on table da.UslovniPredmet
to public;

grant select
on table da.StudentskiStatus
to public;

grant select
on table da.Dosije
to public;

grant select
on table da.DosijeExt
to public;

grant select
on table da.PriznatIspit
to public;

grant select
on table da.SkolskaGodina
to public;

grant select
on table da.Semestar
to public;

grant select
on table da.Kurs
to public;

grant select
on table da.UpisGodine
to public;

grant select
on table da.UpisanKurs
to public;

grant select
on table da.IspitniRok
to public;

grant select
on table da.Ispit
to public;
----------------------------------------------------------------------------
