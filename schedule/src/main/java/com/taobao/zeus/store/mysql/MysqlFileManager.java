package com.taobao.zeus.store.mysql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.taobao.zeus.model.FileDescriptor;
import com.taobao.zeus.store.FileManager;
import com.taobao.zeus.store.mysql.persistence.FilePersistence;
import com.taobao.zeus.store.mysql.tool.PersistenceAndBeanConvert;

public class MysqlFileManager extends HibernateDaoSupport implements
		FileManager {

	@Override
	public FileDescriptor addFile(String uid, String parentId, String name,
			boolean folder) {
		FilePersistence fp = new FilePersistence();
		fp.setName(name);
		fp.setOwner(uid);
		fp.setParent(Long.valueOf(parentId));
		fp.setType(folder ? FilePersistence.FOLDER : FilePersistence.FILE);
		getHibernateTemplate().save(fp);
		return PersistenceAndBeanConvert.convert(fp);
	}

	@Override
	public void deleteFile(String fileId) {
		FilePersistence fp = (FilePersistence) getHibernateTemplate().get(
				FilePersistence.class, Long.valueOf(fileId));
		getHibernateTemplate().delete(fp);
	}

	@Override
	public FileDescriptor getFile(String id) {
		FilePersistence fp = (FilePersistence) getHibernateTemplate().get(
				FilePersistence.class, Long.valueOf(id));
		if (fp != null) {
			return PersistenceAndBeanConvert.convert(fp);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FileDescriptor> getSubFiles(final String id) {
		List<FileDescriptor> list = (List<FileDescriptor>) getHibernateTemplate()
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("from com.taobao.zeus.store.mysql.persistence.FilePersistence where parent=?");
						query.setParameter(0, Long.valueOf(id));
						List<FilePersistence> fps = query.list();
						List<FileDescriptor> list = new ArrayList<FileDescriptor>();
						for (FilePersistence fp : fps) {
							list.add(PersistenceAndBeanConvert.convert(fp));
						}
						return list;
					}
				});
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FileDescriptor> getUserFiles(final String uid) {
		List<FilePersistence> list = (List<FilePersistence>) getHibernateTemplate()
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("from com.taobao.zeus.store.mysql.persistence.FilePersistence where owner=? and parent=null");
						query.setParameter(0, uid);
						List<FilePersistence> list = query.list();
						if (list == null || list.isEmpty()) {
							if (list == null) {
								list = new ArrayList<FilePersistence>();
							}
							FilePersistence personal = new FilePersistence();
							personal.setName(PERSONAL);
							personal.setOwner(uid);
							personal.setType(FilePersistence.FOLDER);
							session.save(personal);
							FilePersistence common = new FilePersistence();
							common.setName(SHARE);
							common.setOwner(uid);
							common.setType(FilePersistence.FOLDER);
							session.save(common);

							list.add(personal);
							list.add(common);
						}
						return list;
					}
				});
		List<FileDescriptor> result = new ArrayList<FileDescriptor>();
		if (list != null) {
			for (FilePersistence fp : list) {
				result.add(PersistenceAndBeanConvert.convert(fp));
			}
		}
		return result;
	}

	@Override
	public void update(FileDescriptor fd) {
		fd.setGmtModified(new Date());
		getHibernateTemplate().update(PersistenceAndBeanConvert.convert(fd));
	}

}
