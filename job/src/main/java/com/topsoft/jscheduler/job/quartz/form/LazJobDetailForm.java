package com.topsoft.jscheduler.job.quartz.form;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobConfig;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionConfig;
import com.topsoft.jscheduler.job.quartz.domain.LazJobMonitor;
import com.topsoft.jscheduler.job.quartz.domain.LazTrigger;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzExecutionType;
import com.topsoft.jscheduler.job.util.LazJobContext;
import com.topsoft.topframework.base.util.LazImage;
import com.topsoft.topframework.base.validator.Validator;
import com.topsoft.topframework.base.validator.impl.NoSpaceValidator;
import com.topsoft.topframework.swing.LazAlert;
import com.topsoft.topframework.swing.LazButton;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazComboBox;
import com.topsoft.topframework.swing.LazForm;
import com.topsoft.topframework.swing.LazFormView;
import com.topsoft.topframework.swing.LazPanel;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazSwitch;
import com.topsoft.topframework.swing.LazTabbedPane;
import com.topsoft.topframework.swing.LazTable;
import com.topsoft.topframework.swing.LazTextField;
import com.topsoft.topframework.swing.LazViewCapable;
import com.topsoft.topframework.swing.event.LazFormEvent;
import com.topsoft.topframework.swing.event.LazTableEvent;
import com.topsoft.topframework.swing.model.LazTableModel;
import com.topsoft.topframework.swing.table.LazTableColumn;
import com.topsoft.topframework.swing.table.LazTableImageActionColumn;
import com.topsoft.topframework.swing.table.LazTableNestedColumn;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class LazJobDetailForm extends LazForm<LazJobDetail>implements LazViewCapable<LazJobDetail> {

	private static final long serialVersionUID = 1954648989850415955L;

	@Autowired
	private QuartzBO quartzBO;

	private LazTable<LazTrigger> tableExecutions;
	private LazTable<LazJobMonitor> tableMonitors;
	private LazTableModel<LazTrigger> tableModelExecutions;
	private LazTableModel<LazJobMonitor> tableModelMonitors;

	private LazTextField txfJobId, txfGroupId, txfJobDescription;
	private LazComboBox<QuartzExecutionType> cmbExecutionType;
	private LazSwitch swtRunHoliday, swtContinueException;
	private LazButton btnAddExecution, btnAddMonitor;
	private LazPanel panelGeneral, panelExecutions, panelConfig, panelExecution;

	private LazTabbedPane panes;

	@Override
	protected void createForm() {

		setLayout(new MigLayout("fill, ins 0, wrap 1", "[grow,fill]", "[]5[]5[]10[grow,fill]"));

		add(getPanelGeneral());
		add(getPanelConfig());
		add(getPanelExecution());
		add(getPanelTabs(), "grow");
	}

	private LazPanel getPanelGeneral() {

		panelGeneral = new LazPanel(new MigLayout("fill, ins 0, wrap 4", "[][grow,fill][][200]"));

		panelGeneral.add("Job ID: ", txfJobId = new LazTextField(), "grow");
		panelGeneral.add("Group ID: ", txfGroupId = new LazTextField(), "w 200!");
		panelGeneral.add("Description: ", txfJobDescription = new LazTextField(), "span 3, grow");

		txfJobId.setRequired(true);
		txfJobId.addValidators(Validator.use(NoSpaceValidator.class));
		txfJobId.addKeyListener(this);

		txfJobDescription.setRequired(true);

		return panelGeneral;
	}

	private LazPanel getPanelConfig() {

		if (panelConfig == null) {

			panelConfig = new LazPanel(new MigLayout("fillx, ins 0, nogrid"));
			panelConfig
				.add("Runs on holidays: ", "w " + LazJobExecutionConfig.LABEL_WIDTH + "!, tag left", swtRunHoliday = new LazSwitch(), "w pref!, tag left");
			panelConfig
				.add("Continue execution after fail: ", "w pref!, tag right", swtContinueException = new LazSwitch(), "w pref!, tag right");
		}

		return panelConfig;
	}

	private LazPanel getPanelExecution() {

		if (panelExecution == null) {

			panelExecution = new LazPanel(new MigLayout("fillx, ins 0, wrap 2", "[" + LazJobExecutionConfig.LABEL_WIDTH + "][grow,fill]"));
			panelExecution
				.add("Execution type: ", cmbExecutionType = new LazComboBox<QuartzExecutionType>(QuartzExecutionType
					.values()), "grow");

			for (QuartzExecutionType executionType : QuartzExecutionType.values())
				panelExecution.add(executionType.getForm(), "span 2, grow");

			cmbExecutionType.setFirstRowText("");
			cmbExecutionType.setRequired(true);
		}

		return panelExecution;
	}

	public LazTabbedPane getPanelTabs() {

		panes = new LazTabbedPane();
		panes.addTab("Executions", getPanelTriggers());
		panes.addTab("Monitoring", getPanelSMS());

		return panes;
	}

	private LazPanel getPanelTriggers() {

		if (panelExecutions == null) {

			panelExecutions = new LazPanel(new MigLayout("fill, wrap 1", "[grow,fill]", "[grow,fill][baseline,nogrid]"));

			Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
			columns.add(new LazTableNestedColumn("Expression", "cronExpression"));
			columns.add(new LazTableNestedColumn("Description", "description"));
			columns.add(new LazTableImageActionColumn(LazImage.EDIT, "Edit execution"));
			columns.add(new LazTableImageActionColumn(LazImage.REMOVE, "Delete execution"));

			tableModelExecutions = new LazTableModel<LazTrigger>(columns);
			tableExecutions = new LazTable<LazTrigger>(tableModelExecutions);
			tableExecutions.setColumnWidths(new double[]{ .3, .7, 25, 25 });

			panelExecutions.add(new LazScrollPane(tableExecutions), "grow");
			panelExecutions.add(btnAddExecution = new LazButton(LazImage.ADD, "New execution"), "tag right");
		}

		return panelExecutions;
	}

	private LazPanel getPanelSMS() {

		LazPanel panel = new LazPanel(new MigLayout("fill, wrap 1", "[grow,fill]", "[grow,fill][baseline,nogrid]"));

		Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
		columns.add(new LazTableNestedColumn("Name", "user.name"));
		columns.add(new LazTableNestedColumn("Monitoring", "monitoring"));
		columns.add(new LazTableImageActionColumn(LazImage.EDIT, "Edit monitor"));
		columns.add(new LazTableImageActionColumn(LazImage.REMOVE, "Delete monitor"));

		tableModelMonitors = new LazTableModel<LazJobMonitor>(columns);
		tableMonitors = new LazTable<LazJobMonitor>(tableModelMonitors);
		tableMonitors.setColumnWidths(new double[]{ 1, 500, 25, 25 });

		panel.add(new LazScrollPane(tableMonitors), "grow");
		panel.add(btnAddMonitor = new LazButton(LazImage.ADD, "New monitor"), "tag right");

		return panel;
	}

	protected void loadForm() {

		LazJobConfig config = dto.getJobConfig();

		// Panels visiblity
		getPanelExecution().setVisible(!dto.isGroup());
		setRowConstraints(getPanelExecution().isVisible() ? "[]5[]5[]10[grow,fill]" : "[]5[]10[grow,fill]");

		swtRunHoliday.setVisible(!dto.isJobOfGroup());
		swtContinueException.setVisible(dto.isJobOfGroup());

		if (dto.isJobOfGroup() && panes.getTabCount() == 2)
			panes.removeTabAt(0);
		else if (!dto.isJobOfGroup() && panes.getTabCount() == 1)
			panes.insertTab("Executions", null, getPanelTriggers(), null, 0);

		panes.setSelectedIndex(0);

		// Setting data
		txfJobId.setText(dto.getName());
		txfGroupId.setText(dto.isGroup() && dto.isDefaultGroup() ? "" : dto.getGroup());
		txfJobDescription.setText(dto.getDescription());

		txfJobId.setEditable(StringUtils.isBlank(dto.getName()));
		txfGroupId.setEditable(StringUtils.isBlank(dto.getGroup()) && !dto.isGroup());

		swtRunHoliday.setSelected(config.isHolidayExecution(), false);
		swtContinueException.setSelected(config.isContinueOnException(), false);

		cmbExecutionType.setSelectedItem(config.getExecutionType());

		if (cmbExecutionType.getSelectedIndex() > 0)
			cmbExecutionType.getSelectedItem().getForm().refreshFor(config.getExecutionConfig(), isReadOnly());

		tableModelExecutions.setData(dto.getTriggers());
		tableModelMonitors.setData(config.getMonitors());
	}

	private void onEditExecution() {

		if (LazAlert.showQuestion("Edit execution?") == JOptionPane.YES_OPTION) {

			LazTrigger trigger = tableExecutions.getSelectedItem();

			if (trigger != null)
				LazFormView.openForm(this, LazJobContext.getBean(LazTriggerForm.class), trigger);
		}
	}

	private void onDeleteExecution() {

		LazTrigger trigger = tableExecutions.getSelectedItem();

		if (trigger != null) {

			boolean exists = quartzBO.checkExists(trigger.getKey());

			if (LazAlert
				.showQuestion("Dlete execution?" + (exists ? " You will not be able to undo this operation!" : "")) == JOptionPane.YES_OPTION) {

				if (quartzBO.checkExists(trigger.getKey()))
					quartzBO.deleteTrigger(trigger.getKey());

				tableModelExecutions.removeRow(trigger);
			}
		}
	}

	private void onEditMonitor() {

		if (LazAlert.showQuestion("Edit monitor?") == JOptionPane.YES_OPTION) {

			LazJobMonitor monitor = tableMonitors.getSelectedItem();

			if (monitor != null)
				LazFormView.openForm(this, LazJobContext.getBean(LazJobMonitorForm.class), monitor);
		}
	}

	private void onDeleteMonitor() {

		LazJobMonitor monitor = tableMonitors.getSelectedItem();

		if (monitor != null)
			if (LazAlert.showQuestion("Delete monitor?") == JOptionPane.YES_OPTION)
				tableModelMonitors.removeRow(monitor);
	}

	@Override
	public void isFormValid() {

		super.isFormValid();

		if (cmbExecutionType.getSelectedIndex() > 0)
			cmbExecutionType.getSelectedItem().getForm().isFormValid();
	}

	@Override
	protected void saveForm() {

		dto.setDescription(txfJobDescription.getText());
		dto.setKey(new JobKey(txfJobId.getText(), txfGroupId.getText()));

		LazJobConfig config = dto.getJobConfig();
		config.setHolidayExecution(swtRunHoliday.isSelected());
		config.setContinueOnException(swtContinueException.isSelected());
		config.setExecutionType(cmbExecutionType.getSelectedItem());
		config.setMonitors(tableModelMonitors.getData());

		if (cmbExecutionType.getSelectedItem() != null) {
			cmbExecutionType.getSelectedItem().getForm().save();
			config.setExecutionConfig(cmbExecutionType.getSelectedItem().getForm().getDTO());
		}

		quartzBO.saveJob(dto);

		for (LazTrigger trigger : tableModelExecutions.getData())
			quartzBO.saveTrigger(dto, trigger);
	}

	@Override
	public String getTitle() {

		String title = "Job Settings";

		if (dto != null && dto.getKey() != null && dto.getKey().getName() != null)
			return title += ": " + dto.getKey().getName();

		return title;
	}

	@Override
	public boolean isResizable() {
		return false;
	}

	@Override
	public Dimension getSize() {
		return new Dimension(900, dto != null && dto.isGroup() ? 450 : 650);
	}

	@Override
	public LazButtonType[] getButtons() {
		return new LazButtonType[]{ LazButtonType.SAVE, LazButtonType.CANCEL };
	}

	@Override
	public void keyReleased(KeyEvent event) {

		LazJobDetail dto = getDTO();

		if (event.getSource() == txfJobId && dto.isGroup())
			txfGroupId.setText(txfJobId.getText());
	}

	@Override
	public void onSave(LazFormEvent event) {

		if (LazTriggerForm.class.isAssignableFrom(event.getSource().getClass())) {

			LazTriggerForm view = (LazTriggerForm) event.getSource();

			if (tableModelExecutions.indexOf(view.getDTO()) == -1)
				tableModelExecutions.addRow((LazTrigger) view.getDTO());

			view.dispose();
		}
		else if (LazJobMonitorForm.class.isAssignableFrom(event.getSource().getClass())) {

			LazJobMonitorForm view = (LazJobMonitorForm) event.getSource();

			if (tableModelMonitors.indexOf(view.getDTO()) == -1)
				tableModelMonitors.addRow((LazJobMonitor) view.getDTO());

			view.dispose();
		}
	}

	private void onExecutionTypeChanged() {

		for (QuartzExecutionType executionType : QuartzExecutionType.values())
			executionType.getForm().setVisible(false);

		if (cmbExecutionType.getSelectedIndex() > 0) {

			LazJobConfigForm<?> form = cmbExecutionType.getSelectedItem().getForm();
			form.resetForm();
			form.setVisible(true);
		}
	}

	@Override
	public void tableEvent(LazTableEvent event) {

		Object source = event.getSource();

		if (source == tableExecutions && event.getID() == LazTableEvent.IMAGE_CLICKED) {

			if (event.getColumn() == 2)
				onEditExecution();
			else if (event.getColumn() == 3)
				onDeleteExecution();
		}
		else if (source == tableMonitors && event.getID() == LazTableEvent.IMAGE_CLICKED) {

			if (event.getColumn() == 2)
				onEditMonitor();
			else if (event.getColumn() == 3)
				onDeleteMonitor();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		super.actionPerformed(event);

		Object source = event.getSource();

		if (source == btnAddExecution)
			LazFormView.openForm(this, LazJobContext.getBean(LazTriggerForm.class), new LazTrigger());
		else if (source == btnAddMonitor)
			LazFormView.openForm(this, LazJobContext.getBean(LazJobMonitorForm.class), new LazJobMonitor());
		else if (source == cmbExecutionType)
			onExecutionTypeChanged();
	}
}