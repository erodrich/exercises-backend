-- Pre-populate admin user
-- Password: Admin123! (BCrypt encoded)
INSERT INTO users (id, username, email, password, role, created_at) 
VALUES (1, 'admin', 'admin@exercises.com', '$2a$10$xLzJhIvdZXG6k5bvJYmW7.hN8l9LCQYqN5d8fB3EHqJqyYmM8L9Ta', 'ADMIN', CURRENT_TIMESTAMP);

-- Pre-populate Exercise table with common exercises

-- Chest Exercises (CHEST = 0)
INSERT INTO exercises (id, name, muscle_group) VALUES (1, 'Bench Press', 0);
INSERT INTO exercises (id, name, muscle_group) VALUES (2, 'Incline Dumbbell Press', 0);
INSERT INTO exercises (id, name, muscle_group) VALUES (3, 'Dumbbell Flat Press', 0);
INSERT INTO exercises (id, name, muscle_group) VALUES (4, 'Cable Fly', 0);
INSERT INTO exercises (id, name, muscle_group) VALUES (5, 'Push Ups', 0);
INSERT INTO exercises (id, name, muscle_group) VALUES (6, 'Decline Press', 0);

-- Back Exercises (BACK = 1)
INSERT INTO exercises (id, name, muscle_group) VALUES (7, 'Pull Ups', 1);
INSERT INTO exercises (id, name, muscle_group) VALUES (8, 'Bent Over Row', 1);
INSERT INTO exercises (id, name, muscle_group) VALUES (9, 'Deadlift', 1);
INSERT INTO exercises (id, name, muscle_group) VALUES (10, 'Lat Pulldown', 1);
INSERT INTO exercises (id, name, muscle_group) VALUES (11, 'Seated Cable Row', 1);
INSERT INTO exercises (id, name, muscle_group) VALUES (12, 'T-Bar Row', 1);

-- Shoulder Exercises (SHOULDERS = 2)
INSERT INTO exercises (id, name, muscle_group) VALUES (13, 'Overhead Press', 2);
INSERT INTO exercises (id, name, muscle_group) VALUES (14, 'Lateral Raise', 2);
INSERT INTO exercises (id, name, muscle_group) VALUES (15, 'Front Raise', 2);
INSERT INTO exercises (id, name, muscle_group) VALUES (16, 'Rear Delt Fly', 2);
INSERT INTO exercises (id, name, muscle_group) VALUES (17, 'Arnold Press', 2);
INSERT INTO exercises (id, name, muscle_group) VALUES (18, 'Face Pulls', 2);

-- Leg Exercises (LEGS = 3)
INSERT INTO exercises (id, name, muscle_group) VALUES (19, 'Squat', 3);
INSERT INTO exercises (id, name, muscle_group) VALUES (20, 'Leg Press', 3);
INSERT INTO exercises (id, name, muscle_group) VALUES (21, 'Leg Curl', 3);
INSERT INTO exercises (id, name, muscle_group) VALUES (22, 'Leg Extension', 3);
INSERT INTO exercises (id, name, muscle_group) VALUES (23, 'Calf Raise', 3);
INSERT INTO exercises (id, name, muscle_group) VALUES (24, 'Lunges', 3);

-- Bicep Exercises (BICEPS = 4)
INSERT INTO exercises (id, name, muscle_group) VALUES (25, 'Barbell Curl', 4);
INSERT INTO exercises (id, name, muscle_group) VALUES (26, 'Dumbbell Curl', 4);
INSERT INTO exercises (id, name, muscle_group) VALUES (27, 'Hammer Curl', 4);
INSERT INTO exercises (id, name, muscle_group) VALUES (28, 'Preacher Curl', 4);
INSERT INTO exercises (id, name, muscle_group) VALUES (29, 'Cable Curl', 4);

-- Tricep Exercises (TRICEPS = 5)
INSERT INTO exercises (id, name, muscle_group) VALUES (30, 'Tricep Pushdown', 5);
INSERT INTO exercises (id, name, muscle_group) VALUES (31, 'Overhead Tricep Extension', 5);
INSERT INTO exercises (id, name, muscle_group) VALUES (32, 'Dips', 5);
INSERT INTO exercises (id, name, muscle_group) VALUES (33, 'Close Grip Bench Press', 5);
INSERT INTO exercises (id, name, muscle_group) VALUES (34, 'Skull Crushers', 5);
