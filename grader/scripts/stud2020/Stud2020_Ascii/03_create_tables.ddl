----------------------------------------------------------------------------
create table da.NivoKvalifikacije (
	Id		smallint not null,	
	Naziv	varchar(100) not null,

	primary key( Id )
);

create table da.StudijskiProgram (
	Id		integer not null,
	Oznaka	varchar(10) not null,
	Naziv	varchar(50) not null,
	IdNivoa	smallint not null,
	ObimESPB smallint not null
		with default 240
		constraint chk_ObimESPB
		check( ObimESPB between 30 and 300 ),
	Zvanje	varchar(100) not null 
		with default,
	Opis	long varchar,
	
	primary key( Id ),
	foreign key fk_Program_Nivo( IdNivoa )
		references da.NivoKvalifikacije,
	constraint uk_Oznaka 
		unique ( IdNivoa, Oznaka )
);

create table da.Predmet (
	Id		integer not null,
	Oznaka	varchar(20) not null,
	Naziv	varchar(150) not null,	
	ESPB	smallint not null 
		constraint chk_ESPB
		check( ESPB between 1 and 50),

	primary key( Id ),
	constraint uk_Oznaka 
		unique ( Oznaka )	
);

create table da.PredmetPrograma (
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
		references da.Predmet,
	foreign key fk_PP_Program( IdPrograma )
		references da.StudijskiProgram
);

create table da.UslovniPredmet (
	IdPrograma			integer not null,
	IdPredmeta			integer not null,
	IdUslovnogPredmeta	integer not null,

	primary key( IdPrograma, IdPredmeta, IdUslovnogPredmeta ),
	foreign key fk_UP_Predmet( IdPredmeta, IdPrograma )
		references da.PredmetPrograma,
	foreign key fk_UP_UslovniPredmet( IdUslovnogPredmeta, IdPrograma )
		references da.PredmetPrograma
);

create table da.StudentskiStatus (
	Id		smallint not null,
	Naziv	varchar(50) not null,
	Studira smallint not null
		with default 0
		constraint chk_Studira
		check( Studira in (0,1) ),
	
	primary key( Id )
);

create table da.Dosije (
	Indeks			integer not null,
	IdPrograma		integer not null,
	Ime				varchar(50) not null,
	Prezime			varchar(50) not null,
	Pol				char,
	MestoRodjenja 	varchar(50),
	IdStatusa		smallint not null,
	DatUpisa		date not null,
	DatDiplomiranja	date,
	
	primary key( Indeks ),
	foreign key fk_Dosije_Program( IdPrograma )
		references da.StudijskiProgram,
	foreign key fk_Dosije_Status( IdStatusa )
		references da.StudentskiStatus		
);

create table da.DosijeExt (
	Indeks			integer not null,
	Fotografija		blob(10m),
	PodaciClob		clob(10m),
	PodaciXml		Xml,

	primary key( Indeks ),
	foreign key fk_DosijeExt_Dosije( Indeks )
		references da.Dosije
);

create table da.PriznatIspit (
	Indeks			integer not null,
	NazivPredmeta	varchar(200) not null,
	ESPB			smallint not null
		constraint chk_ESPB
		check( ESPB between 1 and 50),
	Ocena			smallint
		constraint chk_ocena
		check( ocena between 6 and 10 or ocena is null ),
		
	primary key( Indeks, NazivPredmeta ),
	foreign key fk_PriznatIspit_Dosije( Indeks )
		references da.Dosije
);

create table da.SkolskaGodina (
	SkGodina	smallint not null	
		constraint chk_SkGodina
		check( SkGodina between 2000 and 2100 ),
	DatPocetka	date not null,
	DatKraja	date not null,

	primary key( SkGodina )
);

create table da.Semestar (
	SkGodina	smallint not null,
	Semestar	smallint not null,
		constraint chk_Semestar 
		check( Semestar in (1,2)),
	
	primary key( SkGodina, Semestar ),
	foreign key fk_Semestar_Godina( SkGodina )
		references da.SkolskaGodina	
);

create table da.Kurs (
	SkGodina	smallint not null,
	Semestar	smallint not null,
	IdPredmeta	integer not null,
	
	primary key( SkGodina, Semestar, IdPredmeta ),
	foreign key fk_Kurs_Predmet( IdPredmeta )
		references da.Predmet,
	foreign key fk_Kurs_Semestar( SkGodina, Semestar )
		references da.Semestar	
);

create table da.UpisGodine (
	Indeks		integer not null,
	SkGodina	smallint not null,
	DatUpisa	date not null,
	IdStatusa	smallint not null,

	primary key( Indeks, SkGodina ),
	foreign key fk_UpisGodine_Dosije( Indeks )
		references da.Dosije,
	foreign key fk_UpisGodine_SkGodina( SkGodina )
		references da.SkolskaGodina,
	foreign key fk_UpisGodine_Status( IdStatusa )
		references da.StudentskiStatus
);

create index da.UpisGodine_GodinaIndeks 
	on da.UpisGodine( SkGodina, Indeks );

create table da.UpisanKurs (
	Indeks		integer not null,
	SkGodina	smallint not null,
	Semestar	smallint not null,	
	IdPredmeta	integer not null,
	
	primary key( Indeks, SkGodina, IdPredmeta ),
	foreign key fk_UK_Dosije( Indeks )
		references da.Dosije,
	foreign key fk_UK_Kurs( SkGodina, Semestar, IdPredmeta )
		references da.Kurs
);

create index da.UpisanKurs_GodinaIndeks 
	on da.UpisanKurs( SkGodina, Semestar, Indeks );

create index da.UpisanKurs_Predmet
	on da.UpisanKurs( IdPredmeta, SkGodina, Semestar, Indeks );

create table da.IspitniRok (
	SkGodina	smallint not null,
	OznakaRoka	varchar(20) not null,
	Naziv		varchar(30) not null,
	DatPocetka	date not null,
	DatKraja	date not null,

	primary key( SkGodina, OznakaRoka ),
	foreign key fk_IspRok_Godina( SkGodina )
		references da.SkolskaGodina
);

create table da.Ispit (
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
		references da.IspitniRok,
	foreign key fk_Ispit_UpisanKurs( Indeks, SkGodina, IdPredmeta )
		references da.UpisanKurs
);

create index da.Ispit_Indeks
	on da.Ispit( Indeks, SkGodina, OznakaRoka, IdPredmeta );

create index da.Ispit_Predmet
	on da.Ispit( IdPredmeta, Indeks, SkGodina, OznakaRoka );

----------------------------------------------------------------------------
