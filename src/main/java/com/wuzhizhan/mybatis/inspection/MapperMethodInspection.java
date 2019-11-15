package com.wuzhizhan.mybatis.inspection;

import com.google.common.base.Optional;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.DomElement;
import com.wuzhizhan.mybatis.annotation.Annotation;
import com.wuzhizhan.mybatis.dom.model.Select;
import com.wuzhizhan.mybatis.generate.StatementGenerator;
import com.wuzhizhan.mybatis.locator.MapperLocator;
import com.wuzhizhan.mybatis.service.JavaService;
import com.wuzhizhan.mybatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanglin
 */
public class MapperMethodInspection extends MapperInspection {
    @Nullable
    @Override
    public ProblemDescriptor[] checkMethod(
            @NotNull final PsiMethod method,
            @NotNull final InspectionManager manager,
            final boolean isOnTheFly) {
        if (!MapperLocator.getInstance(method.getProject()).process(method) ||
            JavaUtils.isAnyAnnotationPresent(method, Annotation.STATEMENT_SYMMETRIES)) {
            return EMPTY_ARRAY;
        }

        final List<ProblemDescriptor> problems = createProblemDescriptors(method, manager, isOnTheFly);
        return problems.toArray(new ProblemDescriptor[0]);
    }

    private List<ProblemDescriptor> createProblemDescriptors(
            final PsiMethod method,
            final InspectionManager manager,
            final boolean isOnTheFly) {
        final List<ProblemDescriptor> problems = new ArrayList<>(2);
        Optional<ProblemDescriptor> optionalProblem = checkStatementExists(method, manager, isOnTheFly);

        if (optionalProblem.isPresent()) {
            problems.add(optionalProblem.get());
        }

        optionalProblem = checkResultType(method, manager, isOnTheFly);

        if (optionalProblem.isPresent()) {
            problems.add(optionalProblem.get());
        }

        return problems;
    }

    private Optional<ProblemDescriptor> checkResultType(
            final PsiMethod method,
            final InspectionManager manager,
            final boolean isOnTheFly) {
        final Optional<DomElement> optionalDomElement =
                JavaService.getInstance(method.getProject())
                           .findStatement(method);

        if (!optionalDomElement.isPresent()) {
            return Optional.absent();
        }

        final DomElement domElement = optionalDomElement.get();

        if (domElement instanceof Select) {
            final Select selectStatement = (Select) domElement;

            if (selectStatement.getResultMap().getValue() != null) {
                return Optional.absent();
            }

            final Optional<PsiClass> methodResultType = StatementGenerator.getSelectResultType(method);
            final PsiClass selectResultType = selectStatement.getResultType().getValue();
            final PsiIdentifier methodName = method.getNameIdentifier();

            if (methodName != null) {
                if (methodResultType.isPresent() && (
                        selectResultType == null ||
                        !methodResultType.get().equals(selectResultType) &&
                        !selectResultType.isInheritor(methodResultType.get(), true))) {
                    return Optional.of(
                            manager.createProblemDescriptor(
                                    methodName,
                                    "Result type doesn't match for Select id=\"#ref\"",
                                    new ResultTypeQuickFix(selectStatement, methodResultType.get()),
                                    ProblemHighlightType.GENERIC_ERROR,
                                    isOnTheFly));
                }

                if (!methodResultType.isPresent() && selectResultType != null) {
                    return Optional.of(
                            manager.createProblemDescriptor(
                                    methodName,
                                    "Result type doesn't match for Select id=\"#ref\"",
                                    (LocalQuickFix) null,
                                    ProblemHighlightType.GENERIC_ERROR,
                                    isOnTheFly));
                }
            }
        }

        return Optional.absent();
    }

    private Optional<ProblemDescriptor> checkStatementExists(
            final PsiMethod method,
            final InspectionManager manager,
            final boolean isOnTheFly) {
        final PsiIdentifier methodName = method.getNameIdentifier();

        if (!JavaService.getInstance(method.getProject()).findStatement(method).isPresent() && null != methodName) {
            return Optional.of(manager.createProblemDescriptor(
                    methodName,
                    "Statement with id=\"#ref\" not defined in mapper XML",
                    new StatementNotExistsQuickFix(method),
                    ProblemHighlightType.GENERIC_ERROR,
                    isOnTheFly));
        }

        return Optional.absent();
    }
}
