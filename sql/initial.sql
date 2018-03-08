CREATE TABLE system_parameter (
  id SERIAL PRIMARY KEY,
  key VARCHAR(50) NOT NULL UNIQUE,
  value VARCHAR(500) NOT NULL
);

ALTER TABLE system_parameter ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT 'CONSOLE_PARAMETER';

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