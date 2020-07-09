-- System Parameters
CREATE TABLE system_parameter (
  id SERIAL PRIMARY KEY,
  key VARCHAR(50) NOT NULL UNIQUE,
  value VARCHAR(500) NOT NULL
);
ALTER TABLE system_parameter ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT 'CONTROL_PANEL';

-- Played Games
CREATE TABLE played_game (
  id SERIAL PRIMARY KEY,
  version VARCHAR(50) NOT NULL,
  result VARCHAR(50) NOT NULL,
  date DATE,
  time TIME,
  my_point INTEGER,
  opponent_point INTEGER,
  point_for_win INTEGER NOT NULL,
  opponent_name VARCHAR(50) NOT NULL,
  website VARCHAR(50) NOT NULL,
  game_history TEXT
);
ALTER TABLE played_game ADD COLUMN channel_id INT NOT NULL DEFAULT 0;
ALTER TABLE played_game DROP COLUMN website;
ALTER TABLE played_game ADD COLUMN level INT NOT NULL DEFAULT 0;
ALTER TABLE played_game ALTER COLUMN level TYPE decimal;

-- Opponent Plays
CREATE TABLE opponent_play (
  id SERIAL PRIMARY KEY,
  game_id int NOT NULL,
  version VARCHAR(50) NOT NULL,
  move_type VARCHAR(50) NOT NULL,
  tile VARCHAR(50) NOT NULL,
  opponent_tiles TEXT NOT NULL
);
ALTER TABLE opponent_play ADD COLUMN possible_play_numbers VARCHAR(200);

-- Args And Values
CREATE TABLE arg_and_value (
  id SERIAL PRIMARY KEY,
  arg REAL NOT NULL,
  value REAL NOT NULL,
  function_name VARCHAR(100) NOT NULL
);

-- Channel
CREATE TABLE channel (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  params TEXT
);

-- Client
CREATE TABLE client (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  params TEXT
);