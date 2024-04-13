CONNECT TO GRADER;

CREATE VIEW USER_STATS AS
SELECT USER_ID, SUM(NO_SUBMISSIONS) TOTAL, SUM(NO_CORRECT_SUBMISSIONS) CORRECT,
       SUM(NO_CORRECT_SUBMISSIONS) * 100.0 / SUM(NO_SUBMISSIONS) PERCENT
FROM SUBMISSIONS
GROUP BY USER_ID;

CREATE VIEW TASK_STATS AS
SELECT TASK_ID, SUM(NO_SUBMISSIONS) TOTAL, SUM(NO_CORRECT_SUBMISSIONS) CORRECT,
       SUM(NO_CORRECT_SUBMISSIONS) * 100.0 / SUM(NO_SUBMISSIONS) PERCENT
FROM SUBMISSIONS
GROUP BY TASK_ID;
