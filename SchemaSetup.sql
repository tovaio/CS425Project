/* Drop the tables if they already exist, so that it won't error if the script tries to redefine them */
drop table if exists parking_lot, parking_spot, "user", reservation, single_reservation, car_plate, "session", "member" cascade;
drop type if exists res_category cascade;
drop function if exists available_spots, available_spots_month cascade;
drop view if exists monthly_revenue, parking_usage, user_sessions cascade;

/* Create enum type for reservation categories */
create type res_category as enum ('online', 'drive-in', 'member');

/* Create tables */
create table parking_lot(
	lot_id			serial			primary key,
	membership_fee	integer			not null,
	reservation_fee integer			not null
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
	foreign key (spot_id) references parking_spot,
	check (end_time > start_time)
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
	user_id			integer			not null,
	"month"			integer			not null,
	"year"			integer			not null,
	plate_id		integer			not null,
	res_id			integer			not null,
	primary key (user_id, "month", "year", res_id),
	foreign key (user_id) references "user",
	foreign key (plate_id) references car_plate,
	foreign key (res_id) references reservation,
	check ("month" between 1 and 12)
);

/* Functions */
create function available_spots(arg_start_time time, arg_end_time time, arg_date date)
	returns table (spot_id integer, spot_number integer, lot_id integer)
as $$
	select *
	from parking_spot 
	where spot_id not in ((
		select spot_id 
		from reservation natural join single_reservation
		where ((start_time >= arg_start_time and start_time < arg_end_time) or (end_time > arg_start_time and end_time <= arg_end_time)) and ("date" = arg_date)
	) union (
		select spot_id
		from reservation natural join "member"
		where ((start_time >= arg_start_time and start_time < arg_end_time) or (end_time > arg_start_time and end_time <= arg_end_time))
			and ("month" = extract(month from cast(arg_date as timestamp))) and ("year" = extract(year from cast(arg_date as timestamp)))
	));
$$
language sql;

create function available_spots_month(arg_start_time time, arg_end_time time, arg_month integer, arg_year integer)
	returns table (spot_id integer, spot_number integer, lot_id integer)
as $$
	select *
	from parking_spot 
	where spot_id not in ((
		select spot_id 
		from reservation natural join single_reservation
		where ((start_time >= arg_start_time and start_time < arg_end_time) or (end_time > arg_start_time and end_time <= arg_end_time))
			and (extract(month from cast("date" as timestamp)) = arg_month) and (extract(year from cast("date" as timestamp)) = arg_year)
	) union (
		select spot_id
		from reservation natural join "member"
		where ((start_time >= arg_start_time and start_time < arg_end_time) or (end_time > arg_start_time and end_time <= arg_end_time))
			and ("month" = arg_month) and ("year" = arg_year)
	));
$$
language sql;

/* Views for the reports 2-3 */
create view monthly_revenue as
	select t.lot_id as lot_id, t."month" as "month", t."year" as "year", sum(fee) as total_revenue
	from (
		select lot_id, extract(month from cast("date" as timestamp)) as "month", extract(year from cast("date" as timestamp)) as "year", reservation_fee as fee
		from (single_reservation natural join reservation natural join parking_spot natural join parking_lot)
		union all
		select lot_id, "month", "year", membership_fee as fee
		from ("member" natural join reservation natural join parking_spot natural join parking_lot)
	) as t
	group by t.lot_id, t."month", t."year";

create view parking_usage as
	select lot_id, category, count(res_id) as "count"
	from (reservation natural join parking_spot)
	group by lot_id, category;

create view user_sessions as
	select user_id, "name", login_time, logout_time
	from "session" natural join "user";

/* Populate our tables with some sample values */
insert into parking_lot 		values (default, 100, 10);
insert into parking_lot 		values (default, 50, 5);
insert into parking_lot 		values (default, 200, 20);
insert into parking_spot 		values (default, 1, 1);
insert into parking_spot 		values (default, 2, 1);
insert into parking_spot 		values (default, 3, 1);
insert into parking_spot 		values (default, 1, 2);
insert into parking_spot 		values (default, 2, 2);
insert into parking_spot 		values (default, 3, 2);
insert into parking_spot 		values (default, 1, 3);
insert into parking_spot 		values (default, 2, 3);
insert into "user" 				values (default, 'Tommy', 'password', true, null);
insert into "user" 				values (default, 'Ibby', 'password', true, null);
insert into "user" 				values (default, 'William', 'password', true, null);
insert into "user" 				values (default, 'Parking Lot Staff', 'password', false, 1);
insert into "user" 				values (default, 'Normal User', 'password', false, null);
insert into "user" 				values (default, 'Member 1', 'password', false, null);
insert into "user" 				values (default, 'Member 2', 'password', false, null);
insert into "user" 				values (default, 'Member 3', 'password', false, null);
insert into "user" 				values (default, 'Member 4', 'password', false, null);
insert into reservation 		values (default, '13:00:00', '14:00:00', 'online', 1);
insert into reservation 		values (default, '15:00:00', '16:00:00', 'online', 3);
insert into reservation 		values (default, '09:00:00', '10:00:00', 'drive-in', 5);
insert into reservation 		values (default, '04:30:00', '05:30:00', 'drive-in', 2);
insert into reservation 		values (default, '18:15:00', '19:15:00', 'online', 2);
insert into reservation 		values (default, '16:00:00', '17:00:00', 'member', 7);
insert into reservation 		values (default, '14:00:00', '15:00:00', 'member', 3);
insert into reservation 		values (default, '09:00:00', '17:00:00', 'member', 8);
insert into reservation 		values (default, '16:00:00', '17:00:00', 'member', 7);
insert into single_reservation 	values (1, '2020-12-2', 5);
insert into single_reservation 	values (2, '2020-12-2', 2);
insert into single_reservation 	values (3, '2020-12-3', 3);
insert into single_reservation 	values (4, '2020-12-4', 5);
insert into single_reservation 	values (5, '2020-12-5', 3);
insert into car_plate 			values (default, 'CS425', false, null);
insert into car_plate 			values (default, 'COOL', false, null);
insert into car_plate 			values (default, 'NICE', false, null);
insert into car_plate 			values (default, 'WHOA', true, '2021-02-05');
insert into "session" 			values (5, '2020-10-16 20:00:00', '2020-10-16 20:05:00');
insert into "member" 			values (6, 12, 2020, 1, 6);
insert into "member" 			values (7, 1, 2021, 2, 7);
insert into "member" 			values (8, 1, 2021, 3, 8);
insert into "member" 			values (9, 2, 2021, 4, 9);
