package com.topsoft.jscheduler.job.quartz.lov;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.topsoft.jscheduler.job.quartz.bo.QuartzUserBO;
import com.topsoft.jscheduler.job.quartz.domain.QuartzUser;
import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.swing.LazButton;
import com.topsoft.topframework.swing.LazButtonType;
import com.topsoft.topframework.swing.LazLabel;
import com.topsoft.topframework.swing.LazPaginator;
import com.topsoft.topframework.swing.LazScrollPane;
import com.topsoft.topframework.swing.LazSearchInput;
import com.topsoft.topframework.swing.LazTable;
import com.topsoft.topframework.swing.LazTextField;
import com.topsoft.topframework.swing.LazView;
import com.topsoft.topframework.swing.event.LazPageEvent;
import com.topsoft.topframework.swing.event.LazTableEvent;
import com.topsoft.topframework.swing.lov.LazLov;
import com.topsoft.topframework.swing.model.LazTableModel;
import com.topsoft.topframework.swing.table.LazTableColumn;
import com.topsoft.topframework.swing.table.LazTableNestedColumn;

import net.miginfocom.swing.MigLayout;

@Lazy
@Component
public class QuartzUserLov extends LazView implements LazLov<QuartzUser> {

	private static final long serialVersionUID = -2925137724836506934L;

	@Autowired
	private QuartzUserBO userBO;

	private LazSearchInput<QuartzUser> caller;
	private LazTable<QuartzUser> table;
	private LazTableModel<QuartzUser> tableModel;
	private LazPaginator paginator;
	private LazTextField txfSearch;
	private LazButton btnSearch;

	public QuartzUserLov() {
		super("Users");
	}

	@Override
	public void createView() {

		setLayout(new MigLayout("fill", "[][grow,fill][]", "[][grow,fill,nogrid][baseline,nogrid]"));

		add(new LazLabel("Nome: "));
		add(txfSearch = new LazTextField(), "grow");
		add(btnSearch = new LazButton(LazButtonType.SEARCH), "wrap");

		Vector<LazTableColumn> columns = new Vector<LazTableColumn>();
		columns.add(new LazTableNestedColumn("User", "userId"));
		columns.add(new LazTableNestedColumn("Name", "name"));
		columns.add(new LazTableNestedColumn("Email", "email"));
		columns.add(new LazTableNestedColumn("Mobile", "cellPhone"));

		tableModel = new LazTableModel<QuartzUser>(columns);

		table = new LazTable<QuartzUser>(tableModel);
		table.setColumnWidths(new double[]{ 100, .5, .5, 150 });

		add(new LazScrollPane(table), "grow, wrap");
		add(paginator = new LazPaginator(), "growx");

		setResizable(false);
		setSize(800, 400);
	}

	public void openLov(LazSearchInput<QuartzUser> caller) {

		this.caller = caller;

		tableModel.removeAll();
		txfSearch.clear();
		paginator.refreshFor(null);

		setVisible(true);
	}

	private void searchByName() {

		if (!txfSearch.getText().equals("")) {

			DataPage<QuartzUser> dataPage = userBO.findPageByName(txfSearch.getText(), paginator.getPage());

			if (dataPage != null) {

				List<QuartzUser> smsUsers = new ArrayList<QuartzUser>();

				for (QuartzUser user : dataPage.getData())
					smsUsers
						.add(new QuartzUser(user.getUsername(), user.getName(), user.getEmail(), user.getCellPhone()));

				tableModel.setData(smsUsers);
				paginator.refreshFor(dataPage);
			}
			else {

				tableModel.removeAll();
				paginator.refreshFor(null);
			}
		}
	}

	@Override
	public void tableEvent(LazTableEvent e) {

		QuartzUser smsUser = table.getSelectedItem();

		if (smsUser != null) {

			caller.setSelectedItem(table.getSelectedItem());
			dispose();
		}
	}

	@Override
	public void pageEvent(LazPageEvent e) {

		searchByName();
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		Object source = event.getSource();

		if (source == txfSearch || source == btnSearch)
			searchByName();
	}

	@Override
	public QuartzUser searchByCode(Object code) {

		if (code != null) {

			QuartzUser user = userBO.findByUserID(code.toString());

			if (user != null)
				return new QuartzUser(user.getUsername(), user.getName(), user.getEmail(), user.getCellPhone());
		}

		return null;
	}
}