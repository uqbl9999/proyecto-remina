-- Fix: V2 seed used hash for "password" instead of "password123"
-- Hash below corresponds to "password123" (bcrypt cost 10)
UPDATE users
SET password = '$2y$10$ckDNRSnP8zhW/9Agjg79muHOf.RV6LePeGO3DfUMQWEldyurr0t2e'
WHERE email IN (
    'admin@demo.com',
    'director@demo.com',
    'docente@demo.com',
    'tesoreria@demo.com',
    'padre@demo.com',
    'promotor@demo.com'
);
