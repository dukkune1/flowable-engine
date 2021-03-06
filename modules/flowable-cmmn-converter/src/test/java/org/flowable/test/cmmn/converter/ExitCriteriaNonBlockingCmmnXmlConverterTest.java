/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.test.cmmn.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.flowable.cmmn.model.Case;
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.Criterion;
import org.flowable.cmmn.model.PlanItem;
import org.flowable.cmmn.model.Sentry;
import org.flowable.cmmn.model.SentryOnPart;
import org.flowable.cmmn.model.Stage;
import org.junit.Test;

/**
 * @author Filip Hrisafov
 */
public class ExitCriteriaNonBlockingCmmnXmlConverterTest extends AbstractConverterTest {

    private static final String CMMN_RESOURCE = "org/flowable/test/cmmn/converter/exit-criteria-non-blocking.cmmn";

    @Test
    public void convertXMLToModel() throws Exception {
        CmmnModel cmmnModel = readXMLFile(CMMN_RESOURCE);
        validateModel(cmmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        CmmnModel cmmnModel = readXMLFile(CMMN_RESOURCE);
        CmmnModel parsedModel = exportAndReadXMLFile(cmmnModel);
        validateModel(parsedModel);
    }

    public void validateModel(CmmnModel cmmnModel) {
        assertThat(cmmnModel).isNotNull();
        assertThat(cmmnModel.getCases()).hasSize(1);

        // Case
        Case caze = cmmnModel.getCases().get(0);
        assertThat(caze.getId()).isEqualTo("exitCriteriaNonBlocking");

        // Plan model
        Stage planModel = caze.getPlanModel();
        assertThat(planModel).isNotNull();
        assertThat(planModel.getId()).isEqualTo("exitCriteriaNonBlockingPlanModel");
        assertThat(planModel.getName()).isEqualTo("Exit Criteria Non Blocking CasePlanModel");

        PlanItem planItemTaskA = cmmnModel.findPlanItem("taskA");
        assertThat(planItemTaskA).isNotNull();
        assertThat(planItemTaskA.getDefinitionRef()).isEqualTo("taskDefinition");


        PlanItem planItemTaskB = cmmnModel.findPlanItem("taskB");
        assertThat(planItemTaskB).isNotNull();
        assertThat(planItemTaskB.getDefinitionRef()).isEqualTo("taskDefinition");
        assertThat(planItemTaskB.getExitCriteria())
                .extracting(Criterion::getId, Criterion::getSentryRef)
                .isEmpty();

        assertThat(planModel.getSentries())
                .extracting(Sentry::getId)
                .containsOnly("sentry");
        assertThat(planModel.getSentries().get(0).getOnParts())
                .extracting(SentryOnPart::getId, SentryOnPart::getSourceRef, SentryOnPart::getStandardEvent)
                .containsOnly(
                        tuple("onPart_1", "taskA", "complete")
                );

        assertThat(cmmnModel.getAssociations()).isEmpty();
    }

}
