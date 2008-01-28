ALTER TABLE KIM_PERSON_QUALIFIED_ROLE_T
ADD CONSTRAINT KIM_PERSON_QUALIFIED_ROLE_FK1 FOREIGN KEY
(
PERSON_ID
)
REFERENCES KIM_PERSONS_T
(
ID
) ENABLE
/
ALTER TABLE KIM_PERSON_QUALIFIED_ROLE_T
ADD CONSTRAINT KIM_PERSON_QUALIFIED_ROLE_FK2 FOREIGN KEY
(
ROLE_ID
)
REFERENCES KIM_ROLES_T
(
ID
) ENABLE
/