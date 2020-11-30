/* Drop the tables if they already exist, so that it won't error if the script tries to redefine them */
drop table if exists parking_lot, parking_spot, "user", reservation, single_reservation, car_plate, "session", "member" cascade;
drop type if exists res_category cascade;

/* Create enum type for reservation categories */
create type res_category as enum ('online', 'drive-in', 'member');

/* Create tables */
create table parking_lot(
	lot_id			serial			primary key,
	membership_fee	integer			not null
);
create table parking_spot(
	spot_id			serial			primary key,
	spot_number		integer			not null,
	lot_id			integer			not null,
	foreign key (lot_id) references parking_lot
);
create table  "user"(
	user_id			serial 			primary key,
	"name"			varchar(30)		not null,
	"password"		varchar(20)		not null,
	is_admin  		boolean			not null,
	lot_id			integer,
	foreign key (lot_id) references parking_lot
);
create table reservation(
	res_id			serial 			primary key,
	start_time		time			not null,
	end_time		time			not null,
	category		res_category	not null,
	spot_id			integer			not null,
	foreign key (spot_id) references parking_spot
);
create table single_reservation(
	res_id			integer			primary key,
	"date"			date			not null,
	user_id			integer			not null,
	foreign key (res_id) references reservation,
	foreign key (user_id) references "user"
);
create table car_plate(
	plate_id		serial			primary key,
	plate_number	varchar(10)		not null,
	is_temp			boolean			not null,
	"date"			date						
);
create table "session"(
	user_id			integer			not null,
	login_time		timestamp		not null,
	logout_time		timestamp		not null,
	primary key (user_id, login_time, logout_time),
	foreign key (user_id) references "user"
);
create table "member"(
	user_id			integer			primary key,
	end_date		date			not null,
	plate_id		integer			not null,
	res_id			integer			not null,
	foreign key (user_id) references "user",
	foreign key (plate_id) references car_plate,
	foreign key (res_id) references reservation
);

/* Populate our tables with some sample values */
insert into parking_lot 		values (default, 100);
insert into parking_spot 		values (default, 1, 1);
insert into "user" 				values (default, 'Tommy Vadakumchery', 'password', true, null);
insert into "user" 				values (default, 'Ibrahim Marou', 'password', true, null);
insert into "user" 				values (default, 'William William', 'password', true, null);
insert into "user" 				values (default, 'Parking Lot Staff', 'password', false, 1);
insert into "user" 				values (default, 'Normal User', 'password', false, null);
insert into "user" 				values (default, 'Member', 'password', false, null);
insert into reservation 		values (default, '13:00:00', '14:00:00', 'online', 1);
insert into reservation 		values (default, '16:00:00', '17:00:00', 'member', 1);
insert into single_reservation 	values (1, '2020-10-17', 5);
insert into car_plate 			values (default, 'CS425', false, null);
insert into "session" 			values (5, '2020-10-16 20:00:00', '2020-10-16 20:05:00');
insert into "member" 			values (6, '2020-12-31', 1, 2);
