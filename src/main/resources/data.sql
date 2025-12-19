-- Pre-populate admin user
-- Password: Admin123! (BCrypt encoded)
INSERT INTO users (id, username, email, password, role, created_at) 
VALUES (1, 'admin', 'admin@exercises.com', '$2a$10$4bJP6cLB76ZcsEJEPUDNr.wwK2yq84CdsuC90qiXrUHQUxtS/ZBse', 'ADMIN', CURRENT_TIMESTAMP);

-- Pre-populate MuscleGroup table
INSERT INTO muscle_groups (id, name, description) VALUES (1, 'CHEST', 'Chest exercises for pectoral muscles');
INSERT INTO muscle_groups (id, name, description) VALUES (2, 'BACK', 'Back exercises for latissimus dorsi and other back muscles');
INSERT INTO muscle_groups (id, name, description) VALUES (3, 'SHOULDERS', 'Shoulder exercises for deltoid muscles');
INSERT INTO muscle_groups (id, name, description) VALUES (4, 'LEGS', 'Leg exercises for quadriceps, hamstrings, and calves');
INSERT INTO muscle_groups (id, name, description) VALUES (5, 'BICEPS', 'Bicep exercises for biceps brachii');
INSERT INTO muscle_groups (id, name, description) VALUES (6, 'TRICEPS', 'Tricep exercises for triceps brachii');

-- Pre-populate Exercise table with common exercises

-- Chest Exercises (muscle_group_id = 1)
INSERT INTO exercises (id, name, muscle_group_id) VALUES (1, 'Bench Press', 1);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (2, 'Incline Dumbbell Press', 1);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (3, 'Dumbbell Flat Press', 1);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (4, 'Cable Fly', 1);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (5, 'Push Ups', 1);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (6, 'Decline Press', 1);

-- Back Exercises (muscle_group_id = 2)
INSERT INTO exercises (id, name, muscle_group_id) VALUES (7, 'Pull Ups', 2);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (8, 'Bent Over Row', 2);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (9, 'Deadlift', 2);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (10, 'Lat Pulldown', 2);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (11, 'Seated Cable Row', 2);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (12, 'T-Bar Row', 2);

-- Shoulder Exercises (muscle_group_id = 3)
INSERT INTO exercises (id, name, muscle_group_id) VALUES (13, 'Overhead Press', 3);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (14, 'Lateral Raise', 3);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (15, 'Front Raise', 3);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (16, 'Rear Delt Fly', 3);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (17, 'Arnold Press', 3);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (18, 'Face Pulls', 3);

-- Leg Exercises (muscle_group_id = 4)
INSERT INTO exercises (id, name, muscle_group_id) VALUES (19, 'Squat', 4);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (20, 'Leg Press', 4);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (21, 'Leg Curl', 4);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (22, 'Leg Extension', 4);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (23, 'Calf Raise', 4);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (24, 'Lunges', 4);

-- Bicep Exercises (muscle_group_id = 5)
INSERT INTO exercises (id, name, muscle_group_id) VALUES (25, 'Barbell Curl', 5);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (26, 'Dumbbell Curl', 5);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (27, 'Hammer Curl', 5);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (28, 'Preacher Curl', 5);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (29, 'Cable Curl', 5);

-- Tricep Exercises (muscle_group_id = 6)
INSERT INTO exercises (id, name, muscle_group_id) VALUES (30, 'Tricep Pushdown', 6);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (31, 'Overhead Tricep Extension', 6);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (32, 'Dips', 6);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (33, 'Close Grip Bench Press', 6);
INSERT INTO exercises (id, name, muscle_group_id) VALUES (34, 'Skull Crushers', 6);
