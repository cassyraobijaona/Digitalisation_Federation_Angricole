ALTER TABLE activity_attendance ADD COLUMN attendance_date DATE;
INSERT INTO collectivity_activity (id, collectivity_id, label, activity_type, week_ordinal, day_of_week, executive_date) VALUES
                                                                                                                             ('act-1', 'col-1', 'AG1',               'MEETING',  1,    'SA',  NULL),
                                                                                                                             ('act-2', 'col-1', 'Formation de base',  'TRAINING', 2,    'SU',  NULL),
                                                                                                                             ('act-3', 'col-2', 'AG2',               'MEETING',  1,    'SU',  NULL),
                                                                                                                             ('act-4', 'col-2', 'Formation de base',  'TRAINING', 3,    'SU',  NULL),
                                                                                                                             ('act-5', 'col-2', 'Perfectionnement',   'OTHER',    NULL, NULL,  '2026-04-30'),
                                                                                                                             ('act-6', 'col-3', 'AG3',               'MEETING',  1,    'FR',  NULL),
                                                                                                                             ('act-7', 'col-3', 'Formation de base',  'TRAINING', 4,    'WE',  NULL);
INSERT INTO activity_occupation_concerned (activity_id, occupation) VALUES
                                                                        ('act-1', 'JUNIOR'),('act-1', 'SENIOR'),('act-1', 'SECRETARY'),
                                                                        ('act-1', 'TREASURER'),('act-1', 'VICE_PRESIDENT'),('act-1', 'PRESIDENT'),
                                                                        ('act-2', 'JUNIOR'),
                                                                        ('act-3', 'JUNIOR'),('act-3', 'SENIOR'),('act-3', 'SECRETARY'),
                                                                        ('act-3', 'TREASURER'),('act-3', 'VICE_PRESIDENT'),('act-3', 'PRESIDENT'),
                                                                        ('act-4', 'JUNIOR'),
                                                                        ('act-5', 'SENIOR'),
                                                                        ('act-6', 'JUNIOR'),('act-6', 'SENIOR'),('act-6', 'SECRETARY'),
                                                                        ('act-6', 'TREASURER'),('act-6', 'VICE_PRESIDENT'),('act-6', 'PRESIDENT'),
                                                                        ('act-7', 'JUNIOR');
INSERT INTO activity_attendance (id, activity_id, member_id, attendance_status, attendance_date) VALUES
                                                                                                     ('att-act1-m1-mar', 'act-1', 'C1-M1', 'ATTENDED',  '2026-03-07'),
                                                                                                     ('att-act1-m2-mar', 'act-1', 'C1-M2', 'ATTENDED',  '2026-03-07'),
                                                                                                     ('att-act1-m3-mar', 'act-1', 'C1-M3', 'ATTENDED',  '2026-03-07'),
                                                                                                     ('att-act1-m4-mar', 'act-1', 'C1-M4', 'ATTENDED',  '2026-03-07'),
                                                                                                     ('att-act1-m5-mar', 'act-1', 'C1-M5', 'ATTENDED',  '2026-03-07'),
                                                                                                     ('att-act1-m6-mar', 'act-1', 'C1-M6', 'ATTENDED',  '2026-03-07'),
                                                                                                     ('att-act1-m7-mar', 'act-1', 'C1-M7', 'MISSING',   '2026-03-07'),
                                                                                                     ('att-act1-m8-mar', 'act-1', 'C1-M8', 'MISSING',   '2026-03-07');
INSERT INTO activity_attendance (id, activity_id, member_id, attendance_status, attendance_date) VALUES
                                                                                                     ('att-act1-m1-avr', 'act-1', 'C1-M1', 'ATTENDED',  '2026-04-04'),
                                                                                                     ('att-act1-m2-avr', 'act-1', 'C1-M2', 'ATTENDED',  '2026-04-04'),
                                                                                                     ('att-act1-m3-avr', 'act-1', 'C1-M3', 'MISSING',   '2026-04-04'),
                                                                                                     ('att-act1-m4-avr', 'act-1', 'C1-M4', 'MISSING',   '2026-04-04'),
                                                                                                     ('att-act1-m5-avr', 'act-1', 'C1-M5', 'ATTENDED',  '2026-04-04'),
                                                                                                     ('att-act1-m6-avr', 'act-1', 'C1-M6', 'ATTENDED',  '2026-04-04'),
                                                                                                     ('att-act1-m7-avr', 'act-1', 'C1-M7', 'ATTENDED',  '2026-04-04'),
                                                                                                     ('att-act1-m8-avr', 'act-1', 'C1-M8', 'ATTENDED',  '2026-04-04');
INSERT INTO activity_attendance (id, activity_id, member_id, attendance_status, attendance_date) VALUES
                                                                                                     ('att-act3-m1-mar', 'act-3', 'C2-M1', 'ATTENDED',  '2026-03-08'),
                                                                                                     ('att-act3-m2-mar', 'act-3', 'C2-M2', 'ATTENDED',  '2026-03-08'),
                                                                                                     ('att-act3-m3-mar', 'act-3', 'C2-M3', 'MISSING',   '2026-03-08'),
                                                                                                     ('att-act3-m4-mar', 'act-3', 'C2-M4', 'MISSING',   '2026-03-08'),
                                                                                                     ('att-act3-m5-mar', 'act-3', 'C2-M5', 'ATTENDED',  '2026-03-08'),
                                                                                                     ('att-act3-m6-mar', 'act-3', 'C2-M6', 'ATTENDED',  '2026-03-08'),
                                                                                                     ('att-act3-m7-mar', 'act-3', 'C2-M7', 'ATTENDED',  '2026-03-08'),
                                                                                                     ('att-act3-m8-mar', 'act-3', 'C2-M8', 'ATTENDED',  '2026-03-08');
INSERT INTO activity_attendance (id, activity_id, member_id, attendance_status, attendance_date) VALUES
                                                                                                     ('att-act3-m1-avr', 'act-3', 'C2-M1', 'ATTENDED',  '2026-04-05'),
                                                                                                     ('att-act3-m2-avr', 'act-3', 'C2-M2', 'ATTENDED',  '2026-04-05'),
                                                                                                     ('att-act3-m3-avr', 'act-3', 'C2-M3', 'MISSING',   '2026-04-05'),
                                                                                                     ('att-act3-m4-avr', 'act-3', 'C2-M4', 'ATTENDED',  '2026-04-05'),
                                                                                                     ('att-act3-m5-avr', 'act-3', 'C2-M5', 'ATTENDED',  '2026-04-05'),
                                                                                                     ('att-act3-m6-avr', 'act-3', 'C2-M6', 'ATTENDED',  '2026-04-05'),
                                                                                                     ('att-act3-m7-avr', 'act-3', 'C2-M7', 'ATTENDED',  '2026-04-05'),
                                                                                                     ('att-act3-m8-avr', 'act-3', 'C2-M8', 'MISSING',   '2026-04-05');
INSERT INTO activity_attendance (id, activity_id, member_id, attendance_status, attendance_date) VALUES
                                                                                                     ('att-act5-m1', 'act-5', 'C2-M1', 'ATTENDED',   '2026-04-30'),
                                                                                                     ('att-act5-m2', 'act-5', 'C2-M2', 'ATTENDED',   '2026-04-30'),
                                                                                                     ('att-act5-m3', 'act-5', 'C2-M3', 'ATTENDED',   '2026-04-30'),
                                                                                                     ('att-act5-m4', 'act-5', 'C2-M4', 'MISSING',    '2026-04-30'),
                                                                                                     ('att-act5-m5', 'act-5', 'C2-M5', 'UNDEFINED',  '2026-04-30'),
                                                                                                     ('att-act5-m6', 'act-5', 'C2-M6', 'UNDEFINED',  '2026-04-30'),
                                                                                                     ('att-act5-m7', 'act-5', 'C2-M7', 'UNDEFINED',  '2026-04-30'),
                                                                                                     ('att-act5-m8', 'act-5', 'C2-M8', 'UNDEFINED',  '2026-04-30');
INSERT INTO activity_attendance (id, activity_id, member_id, attendance_status, attendance_date) VALUES
                                                                                                     ('att-act6-m1-mar', 'act-6', 'C3-M1', 'ATTENDED',  '2026-03-06'),
                                                                                                     ('att-act6-m2-mar', 'act-6', 'C3-M2', 'ATTENDED',  '2026-03-06'),
                                                                                                     ('att-act6-m3-mar', 'act-6', 'C3-M3', 'ATTENDED',  '2026-03-06'),
                                                                                                     ('att-act6-m4-mar', 'act-6', 'C3-M4', 'ATTENDED',  '2026-03-06'),
                                                                                                     ('att-act6-m5-mar', 'act-6', 'C3-M5', 'ATTENDED',  '2026-03-06'),
                                                                                                     ('att-act6-m6-mar', 'act-6', 'C3-M6', 'ATTENDED',  '2026-03-06'),
                                                                                                     ('att-act6-m7-mar', 'act-6', 'C3-M7', 'MISSING',   '2026-03-06'),
                                                                                                     ('att-act6-m8-mar', 'act-6', 'C3-M8', 'MISSING',   '2026-03-06');
INSERT INTO activity_attendance (id, activity_id, member_id, attendance_status, attendance_date) VALUES
                                                                                                     ('att-act6-m1-avr', 'act-6', 'C3-M1', 'ATTENDED',  '2026-04-03'),
                                                                                                     ('att-act6-m2-avr', 'act-6', 'C3-M2', 'ATTENDED',  '2026-04-03'),
                                                                                                     ('att-act6-m3-avr', 'act-6', 'C3-M3', 'MISSING',   '2026-04-03'),
                                                                                                     ('att-act6-m4-avr', 'act-6', 'C3-M4', 'MISSING',   '2026-04-03'),
                                                                                                     ('att-act6-m5-avr', 'act-6', 'C3-M5', 'ATTENDED',  '2026-04-03'),
                                                                                                     ('att-act6-m6-avr', 'act-6', 'C3-M6', 'ATTENDED',  '2026-04-03'),
                                                                                                     ('att-act6-m7-avr', 'act-6', 'C3-M7', 'MISSING',   '2026-04-03'),
                                                                                                     ('att-act6-m8-avr', 'act-6', 'C3-M8', 'ATTENDED',  '2026-04-03'),
                                                                                                     ('att-act6-c1m1-avr','act-6','C1-M1', 'ATTENDED',  '2026-04-03');
