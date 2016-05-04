package com.topsoft.jscheduler.job.quartz.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.JobDetail;
import org.quartz.utils.Key;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.alee.extended.layout.ToolbarLayout;
import com.alee.extended.panel.CenterPanel;
import com.alee.extended.panel.GroupPanel;
import com.alee.extended.statusbar.WebMemoryBar;
import com.alee.extended.statusbar.WebStatusBar;
import com.alee.laf.separator.WebSeparator;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotificationPopup;
import com.topsoft.jscheduler.job.quartz.bo.QuartzBO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobDetail;
import com.topsoft.jscheduler.job.quartz.domain.LazJobGroup;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.domain.LazTrigger;
import com.topsoft.jscheduler.job.quartz.domain.type.LazTriggerState;
import com.topsoft.jscheduler.job.quartz.domain.type.QuartzJobType;
import com.topsoft.jscheduler.job.quartz.event.QuartzEvent;
import com.topsoft.jscheduler.job.quartz.event.QuartzListener;
import com.topsoft.jscheduler.job.quartz.form.LazJobDetailForm;
import com.topsoft.jscheduler.job.quartz.form.LazJobParameterForm;
import com.topsoft.jscheduler.job.util.LazJobContext;
import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.topframework.base.security.SecurityContext;
import com.topsoft.topframework.base.util.LazImage;
import com.topsoft.topframework.swing.LazAlert;
import com.topsoft.topframework.swing.LazButton;
import com.topsoft.topframework.swing.LazFormView;
import com.topsoft.topframework.swing.LazLabel;
import com.topsoft.topframework.swing.LazMenu;
import com.topsoft.topframework.swing.LazMenuItem;
import com.topsoft.topframework.swing.LazNotification;
import com.topsoft.topframework.swing.LazPanel;
import com.topsoft.topframework.swing.LazPopupMenu;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazToggleButton;
import com.topsoft.topframework.swing.LazTreeTable;
import com.topsoft.topframework.swing.LazView;
import com.topsoft.topframework.swing.event.LazFormEvent;
import com.topsoft.topframework.swing.event.LazFormListener;
import com.topsoft.topframework.swing.event.LazMenuItemEvent;
import com.topsoft.topframework.swing.event.LazMenuItemListener;
import com.topsoft.topframework.swing.event.LazTableEvent;
import com.topsoft.topframework.swing.fonts.LazFonts;
import com.topsoft.topframework.swing.model.LazTreeTableModel;
import com.topsoft.topframework.swing.table.LazTableColumn;
import com.topsoft.topframework.swing.table.LazTableImageActionColumn;
import com.topsoft.topframework.swing.table.LazTableImageColumn;
import com.topsoft.topframework.swing.table.LazTableNestedColumn;
import com.topsoft.topframework.swing.table.renderer.LazImageCellRenderer;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class QuartzView extends LazPanel implements LazMenuItemListener, LazFormListener, PopupMenuListener, QuartzListener, ApplicationContextAware {

	private static final long serialVersionUID = -4391066062440076278L;

	@Autowired
	private QuartzBO quartzBO;

	@Autowired
	private SecurityContext security;

	private LazPopupMenu menu;
	private LazMenu mnuOrdem;
	private LazMenuItem mnuAddJob, mnuRemoveJob, mnuConfig, mnuStart, mnuStop, mnuLastExecutions, mnuNextExecutions,
			mnuJobUp, mnuJobDown;
	private WebSeparator mnuSeparator1, mnuSeparator2, mnuSeparator3;

	private LazTreeTableModel<LazJobDetail> treeTableModel;
	private LazTreeTable<LazJobDetail> treeTable;
	private LazLabel btnRefresh, btnSettings;
	private LazToggleButton tbStandByMode;
	private boolean standBy, readOnly;

	public QuartzView() {
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {

		setLayout(new MigLayout("fill, ins 5"));

		standBy = quartzBO.isInStandbyMode();
		readOnly = quartzBO.isReadOnly();

		add(getPanelHeader(), "north");
		add(getTabbedPane(), "grow, center");
		add(getStatusBar(), "south");

		addListeners();
	}

	@Override
	protected void addListeners() {

		super.addListeners();

		if (!hasEvent(quartzBO, this))
			quartzBO.addActionListener(this);
	}

	private LazPanel getPanelHeader() {

		Font font12 = LazFonts.BASE_FONT.deriveFont(12f);

		LazPanel panelStatus = new LazPanel(new MigLayout("alignx trailing"), new Color(220, 220, 220));
		panelStatus.add(new LazLabel(new ImageIcon(getClass().getResource("/images/header/date.png"))));
		panelStatus.add(new LazLabel(new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy")
			.format(Calendar.getInstance().getTime()), font12), "gapleft 5, gapright 10");
		panelStatus.add(new LazLabel(new ImageIcon(getClass().getResource("/images/header/user.png"))));
		panelStatus.add(new LazLabel(security.getLoggedUser().getName().trim() + " (" + security.getEnvironment()
			.name() + ")", font12), "gapleft 5, gapright 10");
		panelStatus.add(new LazLabel(new ImageIcon(getClass().getResource("/images/header/version.png"))));
		panelStatus.add(new LazLabel(loadVersion(), font12), "gapleft 5");

		LazPanel panelTitle = new LazPanel(new MigLayout("fill, wrap 1"), new Color(0, 86, 161));
		panelTitle
			.add(new LazLabel("CHUBB INSURANCE", new Font("HelveticaNeue LT 63 MdEx Heavy", Font.PLAIN, 20), security
				.getEnvironment().isProduction() ? Color.white : Color.red));
		panelTitle.add(new LazLabel(quartzBO.getSchedulerName(), Color.white));

		LazPanel panelHeader = new LazPanel(new MigLayout("fill, ins 0, gap 0, wrap 1", "[grow,fill]", "[][grow,fill][]"));
		panelHeader.add(panelStatus, "h 30!");
		panelHeader.add(panelTitle, "grow");
		panelHeader.add(new LazPanel(new Color(190, 190, 190)), "h 30!");

		LazPanel panel = new LazPanel(new MigLayout("fill, ins 0, gap 0", "[][grow,fill]"), Color.white);
		panel.add(new LazLabel(new ImageIcon(getClass().getResource("/images/header/logo.png"))));
		panel.add(panelHeader, "grow");

		return panel;
	}

	private String loadVersion() {

		InputStream input = getClass().getResourceAsStream("/version.properties");

		if (input == null)
			return null;

		try {

			Properties props = new Properties();
			props.load(input);
			input.close();

			return (String) props.get("lazuw.version");
		}
		catch (IOException e1) {
			return null;
		}
	}

	public LazPanel getTabbedPane() {

		LazPanel panelTable = new LazPanel(new MigLayout("fill, ins 10", "[grow,fill]", "[grow,fill][]"));

		Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
		columns.add(new LazTableNestedColumn("Service", "key.name", LazTreeTableModel.class));
		columns.add(new LazTableNestedColumn("Description", "description"));
		columns.add(new LazTableNestedColumn("Execution at", "nextTrigger.description"));
		columns.add(new LazTableNestedColumn("Last execution", "formattedPreviousFireTime", SwingConstants.CENTER));
		columns.add(new LazTableNestedColumn("Next execution", "formattedNextFireTime", SwingConstants.CENTER));
		columns.add(new LazTableImageActionColumn(new RunImageCellRenderer(LazImage.RUN, "Executar Job")));
		columns.add(new LazTableImageColumn("stateIcon", "stateTooltip"));

		treeTableModel = new LazTreeTableModel<LazJobDetail>("Schedulers", columns) {

			private static final long serialVersionUID = 4126466964830446575L;

			@Override
			public Object getChild(Object node, int i) {

				if (node == getRoot())
					return getData().get(i);
				else if (LazJobGroup.class.isAssignableFrom(node.getClass()))
					return ((LazJobGroup) node).getJobs().get(i);

				return null;
			}

			@Override
			public int getChildCount(Object node) {

				if (node == null || getData() == null)
					return 0;
				else if (node == getRoot())
					return getData().size();
				else if (LazJobGroup.class.isAssignableFrom(node.getClass()))
					return ((LazJobGroup) node).getJobs().size();

				return 0;
			}
		};

		treeTable = new LazTreeTable<LazJobDetail>(treeTableModel) {

			private static final long serialVersionUID = 7471314371512678276L;

			@Override
			protected boolean isShowPopupAllowed() {
				return getSelectedItem() != null;
			}

			@Override
			public java.awt.Component prepareRenderer(TableCellRenderer renderer, int row, int col) {

				java.awt.Component comp = super.prepareRenderer(renderer, row, col);

				if (col == 5) {

					Object rowObj = treeTableModel.getRowElementAt(row);
					comp.setVisible(rowObj != null && rowObj instanceof JobDetail);
				}

				return comp;
			}
		};

		panelTable.add(new LazScrollPane(treeTable), "grow");
		treeTable.setColumnWidths(new double[]{ 230, .5, .5, 140, 140, 25, 25 });
		treeTable.setComponentPopupMenu(getMenu());

		WebTabbedPane tabPane = new WebTabbedPane();
		tabPane.addTab("Schedulers", panelTable);

		LazPanel panelButtons = new LazPanel(new MigLayout("ins 0, gap 10"));
		panelButtons.add(btnRefresh = new LazLabel(LazImage.REFRESH, "Atualizar lista", this));
		panelButtons.add(btnSettings = new LazLabel(LazImage.SETTINGS, "Settings", this), "gapright 5");

		LazPanel panelReturn = new LazPanel(new MigLayout("fill, ins 0, wrap 1", "[grow,fill]"));
		panelReturn.add(panelButtons, "h 20!, pos 1al 0al");
		panelReturn.add(tabPane, "grow");

		return panelReturn;
	}

	private class RunImageCellRenderer extends LazImageCellRenderer {

		private static final long serialVersionUID = 4982991535356653265L;

		public RunImageCellRenderer(LazImage image, String toolTip) {

			super(image.getIcon(), toolTip);
		}

		public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			Object rowObj = treeTableModel.getRowElementAt(row);

			if (rowObj != null && LazJobGroup.class.isAssignableFrom(rowObj.getClass()))
				setVisible(((LazJobGroup) rowObj).getState() != null);
			else
				setVisible(rowObj != null && rowObj instanceof JobDetail);

			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}

	private LazPopupMenu getMenu() {

		menu = new LazPopupMenu();
		menu.add(mnuConfig = new LazMenuItem("Settings", LazImage.SETTINGS));
		menu.add(mnuAddJob = new LazMenuItem("New Group", LazImage.ADD));
		menu.add(mnuRemoveJob = new LazMenuItem("Remove Job", LazImage.REMOVE));
		menu.add(mnuSeparator1 = new WebSeparator());
		menu.add(mnuStart = new LazMenuItem("Start Service", LazImage.ON));
		menu.add(mnuStop = new LazMenuItem("Pause Service", LazImage.OFF));
		menu.add(mnuSeparator2 = new WebSeparator());
		menu.add(mnuLastExecutions = new LazMenuItem("Last executions", LazImage.LEFT));
		menu.add(mnuNextExecutions = new LazMenuItem("Next executions", LazImage.RIGHT));
		menu.add(mnuSeparator3 = new WebSeparator());
		menu.add(mnuOrdem = new LazMenu("Order", LazImage.MOVE));
		mnuOrdem.add(mnuJobUp = new LazMenuItem("Move up", LazImage.UP));
		mnuOrdem.add(mnuJobDown = new LazMenuItem("Move down", LazImage.DOWN));

		menu.addMenuItemListener(this);
		menu.addPopupMenuListener(this);
		mnuOrdem.addMenuItemListener(this);

		return menu;
	}

	public WebStatusBar getStatusBar() {

		WebStatusBar statusBar = new WebStatusBar();
		statusBar.add(tbStandByMode = new LazToggleButton(LazImage.OFF, "Server off"));

		WebMemoryBar memoryBar = new WebMemoryBar();
		memoryBar.setPreferredWidth(memoryBar.getPreferredSize().width + 20);
		statusBar.add(memoryBar, ToolbarLayout.END);

		tbStandByMode.addActionListener(this);

		return statusBar;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void refreshJobs() {

		if (quartzBO != null) {

			tbStandByMode.setText("Server " + (readOnly ? "read-only mode" : (standBy ? "off" : "on")));
			tbStandByMode.setSelected(!standBy);
			tbStandByMode.setEnabled(!readOnly);
			tbStandByMode.setIcon((standBy ? LazImage.OFF : LazImage.ON).getIcon());

			treeTable.setEnabled(!standBy && !readOnly);

			treeTableModel.setData(quartzBO.findAllJobs());
			treeTable.setTreeModel(treeTableModel);
		}
	}

	private void onRunClicked() {

		LazJobDetail job = quartzBO.findJobByKey(treeTable.getSelectedItem().getKey());

		if (job != null) {

			if (LazAlert.showQuestion("Execute service " + job.getKey()
				.getName() + "?", "Confirmation") == JOptionPane.YES_OPTION) {

				try {

					Set<LazJobParam> params = job.getJobParams();

					if (params != null && !params.isEmpty()) {

						LazJobParameterForm view = LazJobContext.getBean(LazJobParameterForm.class);
						view.setJob(job);

						LazFormView.openForm(view, new ArrayList<LazJobParam>(params));
					}
					else {

						quartzBO.runJob(job);
					}
				}
				catch (Exception e) {
					LazAlert.showError("Error while executing Job " + job.getKey().getName() + ": " + e
						.getMessage(), "Executing Job");
				}
			}
		}
	}

	private void onAddJob() {

		LazJobDetail job = new LazJobDetail();

		Object selectedItem = treeTable.getSelectedItem();

		if (JobDetail.class.isAssignableFrom(selectedItem.getClass())) {

			job.setGroup(treeTable.getSelectedItem().getKey().getGroup());
			job.getJobConfig()
				.setJobType(job.isDefaultGroup() ? QuartzJobType.STAND_ALONE : QuartzJobType.JOB_OF_GROUP);

			if (job.isJobOfGroup())
				job.setJobGroup((LazJobGroup) treeTable.getSelectedItem());
		}

		LazFormView.openForm(this, LazJobContext.getBean(LazJobDetailForm.class), job, readOnly);
	}

	private void onRemoveJob() {

		LazJobDetail job = treeTable.getSelectedItem();

		if (LazAlert.showQuestion("Remove Job " + job.getKey()
			.getName() + "? You will not be able to undo this operation!") == JOptionPane.YES_OPTION) {

			quartzBO.deleteJob(job);
			refreshJobs();
		}
	}

	private void onJobConfig() {

		LazJobDetail job = quartzBO.findJobByKey(treeTable.getSelectedItem().getKey());

		if (job != null)
			LazFormView.openForm(this, LazJobContext.getBean(LazJobDetailForm.class), job, readOnly);
	}

	private void onStartService() {

		LazJobDetail job = treeTable.getSelectedItem();

		if (LazAlert.showQuestion("Execute service " + job.getKey().getName() + "?") == JOptionPane.YES_OPTION) {

			quartzBO.startJob(job);
			refreshJobs();
		}
	}

	private void onPauseService() {

		LazJobDetail job = treeTable.getSelectedItem();

		if (LazAlert.showQuestion("Pause service " + job.getKey().getName() + "?") == JOptionPane.YES_OPTION) {

			quartzBO.pauseJob(job);
			refreshJobs();
		}
	}

	private void onLastExecutions() {

		LazJobDetail job = quartzBO.findJobByKey(treeTable.getSelectedItem().getKey());

		if (job != null)
			LazFormView.openForm(LazJobContext.getBean(LazJobLastExecutionsView.class), job, readOnly);
	}

	private void onNextExecutions() {

		LazJobDetail job = quartzBO.findJobByKey(treeTable.getSelectedItem().getKey());

		if (job != null)
			LazFormView.openForm(LazJobContext.getBean(LazJobNextExecutionsView.class), job, readOnly);
	}

	private void onMoveJobUp() {
		onMoveJob(SwingConstants.TOP);
	}

	private void onMoveJobDown() {
		onMoveJob(SwingConstants.BOTTOM);
	}

	private void onMoveJob(int direction) {

		LazJobDetail job = treeTable.getSelectedItem();

		if (job != null && job.isJobOfGroup())
			quartzBO.moveJob(job, direction);

		refreshJobs();
	}

	private void onChangeStandBy() {

		standBy = !tbStandByMode.isSelected();
		quartzBO.setQuartzStandbyMode(standBy);

		refreshJobs();
	}

	@Override
	public void onLoad(LazFormEvent event) {
	}

	@Override
	public void onBeforeSave(LazFormEvent event) throws BusinessException {
	}

	@Override
	public void onSave(LazFormEvent event) {

		if (LazJobDetailForm.class.isAssignableFrom(event.getSource().getClass()))
			refreshJobs();
	}

	@Override
	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

		menu.setAllItemsVisible(false);

		Object item = treeTable.getSelectedItem();

		if (item != null) {

			mnuAddJob.setVisible(!readOnly);
			mnuAddJob.setText(treeTableModel.getRoot().equals(item) ? "New Group" : "New Job");

			if (LazJobDetail.class.isAssignableFrom(item.getClass())) {

				LazJobDetail job = (LazJobDetail) item;

				if (job.isJobOfGroup() || job.isStandAloneJob())
					mnuAddJob.setVisible(false);

				boolean defaultGroup = job.getKey().getName().equals(Key.DEFAULT_GROUP);

				mnuRemoveJob.setVisible(!readOnly && ((job.isJobOfGroup() || job.isStandAloneJob()) || (job
					.isGroup() && job.getJobs().isEmpty())));
				mnuConfig.setVisible(!defaultGroup);
				mnuLastExecutions.setVisible(!job.isJobOfGroup() && !(job.isGroup() && defaultGroup));
				mnuNextExecutions.setVisible(!job.isJobOfGroup() && !(job.isGroup() && defaultGroup));
				mnuStart.setVisible(!readOnly && !defaultGroup);
				mnuStop.setVisible(!readOnly && !defaultGroup);

				mnuStart.setEnabled(!defaultGroup && job.getState() == LazTriggerState.PAUSED);
				mnuStop.setEnabled(!defaultGroup && job.getState() != LazTriggerState.PAUSED);
				mnuNextExecutions.setEnabled(!defaultGroup && job.getState() != LazTriggerState.PAUSED);

				mnuOrdem.setVisible(!readOnly && job.isJobOfGroup());
				mnuJobUp.setEnabled(job.isJobOfGroup() && job.getJobConfig().getJobSequence() > 1);
				mnuJobDown.setEnabled(job.isJobOfGroup() && job.getJobGroup() != null && job.getJobConfig()
					.getJobSequence() < job.getJobGroup().getJobs().size());

				mnuSeparator1.setVisible(!treeTableModel.getRoot().equals(item) && mnuStart.isVisible());
				mnuSeparator2.setVisible(mnuLastExecutions.isVisible());
				mnuSeparator3.setVisible(mnuOrdem.isVisible());
			}
		}
	}

	@Override
	public void menuItemClicked(LazMenuItemEvent event) {

		Object source = event.getSource();

		if (source == mnuConfig)
			onJobConfig();
		else if (source == mnuAddJob)
			onAddJob();
		else if (source == mnuRemoveJob)
			onRemoveJob();
		else if (source == mnuStart)
			onStartService();
		else if (source == mnuStop)
			onPauseService();
		else if (source == mnuLastExecutions)
			onLastExecutions();
		else if (source == mnuNextExecutions)
			onNextExecutions();
		else if (source == mnuJobUp)
			onMoveJobUp();
		else if (source == mnuJobDown)
			onMoveJobDown();
	}

	@Override
	public void tableEvent(LazTableEvent event) {

		if (event.getID() == LazTableEvent.IMAGE_CLICKED && event.getColumn() == 5)
			onRunClicked();
	}

	@Override
	public void jobEvent(final QuartzEvent event) {

		LazJobDetail job = getJobFromTree(event.getJob());

		if (job != null) {

			if (event.getID() == QuartzEvent.JOB_STARTED) {

				job.setState(LazTriggerState.RUNNING);
				LazNotification.showNotification("Job " + job.getNameCompleted() + " initiated successfully");
			}
			else {

				if (event.getID() == QuartzEvent.JOB_FINISHED) {

					LazNotification.showNotification("Job " + job.getNameCompleted() + " finished successfully");
				}
				else if (event.getID() == QuartzEvent.JOB_ERROR) {

					final WebNotificationPopup notificationPopup = new WebNotificationPopup();
					notificationPopup.setIcon(NotificationIcon.error);

					LazLabel label = new LazLabel(" Job " + job.getNameCompleted() + " finished with error.");

					LazButton button = new LazButton("Check execution?");
					button.setRolloverDecoratedOnly(true);
					button.setDrawFocus(false);
					button.setLeftRightSpacing(0);
					button.setBoldFont();
					button.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {

							notificationPopup.hidePopup();
							LazFormView.openForm(LazJobContext.getBean(LazJobLogView.class), event
								.getLog() + "\n" + ExceptionUtils.getStackTrace(event.getException()));
						}
					});

					notificationPopup.setContent(new GroupPanel(2, false, label, new CenterPanel(button, false, true)));
					NotificationManager.showNotification(notificationPopup);

					if (job.isJobOfGroup())
						job.getJobGroup().setState(null);
				}

				job.setState(null);

				LazTrigger trigger = job.getTrigger(event.getJobContext().getTrigger().getKey());

				if (trigger != null)
					trigger.triggered(event.getJobContext().getCalendar());
			}
		}

		treeTable.repaint();
	}

	private LazJobDetail getJobFromTree(LazJobDetail jobDetail) {

		if (treeTableModel != null) {

			for (LazJobDetail job : treeTableModel.getData()) {

				if (job.equals(jobDetail))
					return job;

				for (LazJobDetail jobOfGroup : job.getJobs())
					if (jobOfGroup.equals(jobDetail))
						return jobOfGroup;
			}
		}

		return null;
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		Object source = event.getSource();

		if (source == btnRefresh)
			refreshJobs();
		else if (source == btnSettings)
			LazView.openView(LazJobContext.getBean(QuartzSettingsView.class));
		else if (source == tbStandByMode)
			onChangeStandBy();
	}

	@Override
	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
	}

	@Override
	public void popupMenuCanceled(PopupMenuEvent e) {
	}
}