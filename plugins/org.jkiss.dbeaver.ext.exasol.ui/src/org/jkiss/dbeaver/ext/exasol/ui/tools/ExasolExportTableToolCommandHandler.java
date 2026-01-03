/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2026 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.exasol.ui.tools;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.Log;
import org.jkiss.dbeaver.ext.exasol.model.ExasolSchema;
import org.jkiss.dbeaver.ext.exasol.model.ExasolTable;
import org.jkiss.dbeaver.ext.exasol.model.ExasolTableBase;
import org.jkiss.dbeaver.ext.exasol.model.ExasolView;
import org.jkiss.dbeaver.model.runtime.VoidProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.ui.navigator.NavigatorUtils;
import org.jkiss.utils.CommonUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ExasolExportTableToolCommandHandler extends AbstractHandler {

    private static final Log log = Log.getLog(ExasolExportTableToolCommandHandler.class);

    @Override
    public Object execute(ExecutionEvent event) {
        List<DBSObject> selectedObjects = NavigatorUtils.getSelectedObjects(HandlerUtil.getCurrentSelection(event));
        List<ExasolTable> tables = CommonUtils.filterCollection(selectedObjects, ExasolTable.class);
        List<ExasolView> views = CommonUtils.filterCollection(selectedObjects, ExasolView.class);
        List<ExasolSchema> schemas = CommonUtils.filterCollection(selectedObjects, ExasolSchema.class);

        //add tables for all Schemas but ignore views in schema
        for (ExasolSchema schema : schemas) {
            try {
                tables.addAll(schema.getTables(new VoidProgressMonitor()));
            } catch (DBException e) {
                log.error(e);
            }
        }

        Set<ExasolTableBase> tableBaseObjects = new LinkedHashSet<>();

        //add tables
        tableBaseObjects.addAll(tables);
        //add views
        tableBaseObjects.addAll(views);

        if (!tableBaseObjects.isEmpty()) {
            ExasolExportTableToolDialog dialog = new ExasolExportTableToolDialog(
                HandlerUtil.getActivePart(event).getSite(),
                tableBaseObjects
            );
            return dialog.open();
        }
        return null;
    }

}
