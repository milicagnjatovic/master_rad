----------------------------------------------------------------------------
create table db.NivoKvalifikacije (
	Id		smallint not null,	
	Naziv	varchar(200) not null,

	primary key( Id )
);

create table db.StudijskiProgram (
	Id		integer not null,
	Oznaka	varchar(20) not null,
	Naziv	varchar(100) not null,
	IdNivoa	smallint not null,
	ObimESPB smallint not null
		with default 240
		constraint chk_ObimESPB
		check( ObimESPB between 30 and 300 ),
	Zvanje	varchar(200) not null 
		with default,
	Opis	long varchar,
	
	primary key( Id ),
	foreign key fk_Program_Nivo( IdNivoa )
		references db.NivoKvalifikacije,
	constraint uk_Oznaka 
		unique ( IdNivoa, Oznaka )
);

create table db.Predmet (
	Id		integer not null,
	Oznaka	varchar(40) not null,
	Naziv	varchar(300) not null,	
	ESPB	smallint not null 
		constraint chk_ESPB
		check( ESPB between 1 and 50),

	primary key( Id ),
	constraint uk_Oznaka 
		unique ( Oznaka )	
);

create table db.PredmetPrograma (
	IdPredmeta		integer not null,
	IdPrograma		integer not null,
	Vrsta			varchar(10) not null
		constraint chk_Vrsta
		check( Vrsta in ('obavezan','izborni') ),
	Semestar		smallint
		constraint chk_Semestar
		check( Semestar between 1 and 10 ),

	primary key( IdPredmeta, IdPrograma ),
	foreign key fk_PP_Predmet( IdPredmeta )
		references db.Predmet,
	foreign key fk_PP_Program( IdPrograma )
		references db.StudijskiProgram
);

create table db.UslovniPredmet (
	IdPrograma			integer not null,
	IdPredmeta			integer not null,
	IdUslovnogPredmeta	integer not null,

	primary key( IdPrograma, IdPredmeta, IdUslovnogPredmeta ),
	foreign key fk_UP_Predmet( IdPredmeta, IdPrograma )
		references db.PredmetPrograma,
	foreign key fk_UP_UslovniPredmet( IdUslovnogPredmeta, IdPrograma )
		references db.PredmetPrograma
);

create table db.StudentskiStatus (
	Id		smallint not null,
	Naziv	varchar(120) not null,
	Studira smallint not null
		with default 0
		constraint chk_Studira
		check( Studira in (0,1) ),
	
	primary key( Id )
);

create table db.Dosije (
	Indeks			integer not null,
	IdPrograma		integer not null,
	Ime				varchar(100) not null,
	Prezime			varchar(100) not null,
	Pol				char,
	MestoRodjenja 	varchar(100),
	IdStatusa		smallint not null,
	DatUpisa		date not null,
	DatDiplomiranja	date,
	
	primary key( Indeks ),
	foreign key fk_Dosije_Program( IdPrograma )
		references db.StudijskiProgram,
	foreign key fk_Dosije_Status( IdStatusa )
		references db.StudentskiStatus		
);

create table db.DosijeExt (
	Indeks			integer not null,
	Fotografija		blob(10m),
	PodaciClob		clob(10m),
	PodaciXml		Xml,

	primary key( Indeks ),
	foreign key fk_DosijeExt_Dosije( Indeks )
		references db.Dosije
);

create table db.PriznatIspit (
	Indeks			integer not null,
	NazivPredmeta	varchar(400) not null,
	ESPB			smallint not null
		constraint chk_ESPB
		check( ESPB between 1 and 50),
	Ocena			smallint
		constraint chk_ocena
		check( ocena between 6 and 10 or ocena is null ),
		
	primary key( Indeks, NazivPredmeta ),
	foreign key fk_PriznatIspit_Dosije( Indeks )
		references db.Dosije
);

create table db.SkolskaGodina (
	SkGodina	smallint not null	
		constraint chk_SkGodina
		check( SkGodina between 2000 and 2100 ),
	DatPocetka	date not null,
	DatKraja	date not null,

	primary key( SkGodina )
);

create table db.Semestar (
	SkGodina	smallint not null,
	Semestar	smallint not null,
		constraint chk_Semestar 
		check( Semestar in (1,2)),
	
	primary key( SkGodina, Semestar ),
	foreign key fk_Semestar_Godina( SkGodina )
		references db.SkolskaGodina	
);

create table db.Kurs (
	SkGodina	smallint not null,
	Semestar	smallint not null,
	IdPredmeta	integer not null,
	
	primary key( SkGodina, Semestar, IdPredmeta ),
	foreign key fk_Kurs_Predmet( IdPredmeta )
		references db.Predmet,
	foreign key fk_Kurs_Semestar( SkGodina, Semestar )
		references db.Semestar	
);

create table db.UpisGodine (
	Indeks		integer not null,
	SkGodina	smallint not null,
	DatUpisa	date not null,
	IdStatusa	smallint not null,

	primary key( Indeks, SkGodina ),
	foreign key fk_UpisGodine_Dosije( Indeks )
		references db.Dosije,
	foreign key fk_UpisGodine_SkGodina( SkGodina )
		references db.SkolskaGodina,
	foreign key fk_UpisGodine_Status( IdStatusa )
		references db.StudentskiStatus
);

create index db.UpisGodine_GodinaIndeks 
	on db.UpisGodine( SkGodina, Indeks );

create table db.UpisanKurs (
	Indeks		integer not null,
	SkGodina	smallint not null,
	Semestar	smallint not null,	
	IdPredmeta	integer not null,
	
	primary key( Indeks, SkGodina, IdPredmeta ),
	foreign key fk_UK_Dosije( Indeks )
		references db.Dosije,
	foreign key fk_UK_Kurs( SkGodina, Semestar, IdPredmeta )
		references db.Kurs
);

create index db.UpisanKurs_GodinaIndeks 
	on db.UpisanKurs( SkGodina, Semestar, Indeks );

create index db.UpisanKurs_Predmet
	on db.UpisanKurs( IdPredmeta, SkGodina, Semestar, Indeks );

create table db.IspitniRok (
	SkGodina	smallint not null,
	OznakaRoka	varchar(20) not null,
	Naziv		varchar(60) not null,
	DatPocetka	date not null,
	DatKraja	date not null,

	primary key( SkGodina, OznakaRoka ),
	foreign key fk_IspRok_Godina( SkGodina )
		references db.SkolskaGodina
);

create table db.Ispit (
	SkGodina	smallint not null,
	OznakaRoka	varchar(20) not null,
	Indeks		integer not null,
	IdPredmeta	integer not null,
	Status		char not null 
		with default 'p',
		constraint chk_Status
		check( Status in 
			('p','n','o','d','x','s')),
		-- status prijave moze biti
		--   p - prijavljen
		--   n - nije izasao
		--   o - polagao
		--   d - diskvalifikovan
		--   x - ponisten
		--   s - odustao
	DatPolaganja date,
	Poeni		smallint,
	Ocena		smallint,
		constraint chk_ocena
		check((
		      Status in ('p','n') 
		      and Poeni is null 
		      and Ocena is null
		    )or(
		      Status in ('d','s') 
		      and Poeni<50
		      and Ocena=5
		    )or(
		      Status in ('o','x') 
		      and Poeni between 0 and 100 
		      and Ocena between 5 and 10
		)),
	
	primary key( SkGodina, OznakaRoka, Indeks, IdPredmeta ),
	foreign key fk_Ispit_Rok( SkGodina, OznakaRoka )
		references db.IspitniRok,
	foreign key fk_Ispit_UpisanKurs( Indeks, SkGodina, IdPredmeta )
		references db.UpisanKurs
);

create index db.Ispit_Indeks
	on db.Ispit( Indeks, SkGodina, OznakaRoka, IdPredmeta );

create index db.Ispit_Predmet
	on db.Ispit( IdPredmeta, Indeks, SkGodina, OznakaRoka );

----------------------------------------------------------------------------
