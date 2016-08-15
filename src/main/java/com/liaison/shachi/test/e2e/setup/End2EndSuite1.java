/*
 * Copyright © 2016 Liaison Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.liaison.shachi.test.e2e.setup;

import com.liaison.shachi.model.FamilyModel;
import com.liaison.shachi.model.Name;
import com.liaison.shachi.model.QualModel;
import com.liaison.shachi.model.TableModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Branden Smith; Liaison Technologies, Inc.
 * Created 2015.08.27 13:09
 */
public enum End2EndSuite1 {
    INSTANCE;

    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String CONTEXT_ID_1 = "CONTEXT-1";
    public static final String HANDLE_TESTWRITE_1 = "TEST-WRITE-1";
    public static final String HANDLE_TESTREAD_1 = "TEST-READ-1";
    public static final String TABLENAME_A = End2EndSuite1.class.getSimpleName() + "_A";
    public static final String COLUMNFAMILY_a = "a";
    public static final String COLUMNQUAL_Z = "Z";
    public static final long TS_SAMPLE_1 = 1234567890;

    public static final String CONTEXT_ID_2 = "CONTEXT-2";
    public static final String HANDLE_TESTWRITE_2 = "TEST-WRITE-2";
    public static final String HANDLE_TESTREAD_2 = "TEST-READ-2";
    public static final String TABLENAME_B = End2EndSuite1.class.getSimpleName() + "_B";

    public static final String CONTEXT_ID_3 = "CONTEXT-3";
    public static final String HANDLE_TESTWRITE_3 = "TEST-WRITE-3";
    public static final String HANDLE_TESTREAD_3 = "TEST-READ-3";
    public static final String TABLENAME_C = End2EndSuite1.class.getSimpleName() + "_C";

    public static final List<String> COLUMNQUALS_ABC;

    public static final QualModel QUAL_MODEL_Z =
        QualModel.with(Name.of(COLUMNQUAL_Z)).build();
    public static final FamilyModel FAM_MODEL_a =
        FamilyModel
            .with(Name.of(COLUMNFAMILY_a))
            .qual(QUAL_MODEL_Z)
            .build();

    public static final TableModel TEST_MODEL_A =
        TableModel
            .with(Name.of(TABLENAME_A))
            .family(FAM_MODEL_a)
            .build();
    public static final TableModel TEST_MODEL_B =
        TableModel
            .with(Name.of(TABLENAME_B))
            .family(FAM_MODEL_a)
            .build();
    public static final TableModel TEST_MODEL_C =
        TableModel
            .with(Name.of(TABLENAME_C))
            .family(FAM_MODEL_a)
            .build();

    static {
        final String[] alphabetArray;
        final List<String> alphabetList;

        alphabetArray = new String[ALPHABET.length()];
        Arrays.setAll(alphabetArray,
                      (index) -> {
                          String str = "";
                          for (int iter = 0; iter < 10; iter++) {
                              str += ALPHABET.charAt(index);
                          }
                          return str;
                      });
        alphabetList = Arrays.asList(alphabetArray);
        Collections.shuffle(alphabetList);
        COLUMNQUALS_ABC = Collections.unmodifiableList(alphabetList);
    }
}
