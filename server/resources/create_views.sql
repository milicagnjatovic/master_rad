CONNECT TO GRADER;

CREATE OR REPLACE VIEW USER_STATS AS
SELECT USER_ID, SUM(NO_SUBMISSIONS) TOTAL, SUM(NO_CORRECT_SUBMISSIONS) CORRECT,
       SUM(CASE WHEN NO_CORRECT_SUBMISSIONS > 0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) PERCENT
FROM SUBMISSION
GROUP BY USER_ID;

CREATE OR REPLACE VIEW TASK_STATS AS
SELECT TASK_ID, SUM(NO_SUBMISSIONS) TOTAL, SUM(NO_CORRECT_SUBMISSIONS) CORRECT,
       SUM(CASE WHEN NO_CORRECT_SUBMISSIONS > 0 THEN 1 ELSE 0 END) * 100.0 / COUNT(*) PERCENT
FROM SUBMISSION
GROUP BY TASK_ID;

