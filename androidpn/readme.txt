�����޸Ĵ���ʵ���� ��Ϣ���պͲ鿴�Ļ�ִ�ͽ���������Ϣ������ʱ������ ����û�жԴ�����̫����о������޸ĵĵط����ܲ�����õģ�����ǿ����ʵ���˹��ܡ�
�������и��õķ�����ϣ���ܹ���Ⱥ�ﹲ��һ��  �����һ��ѧϰ

�����������ֻ��ͻ���������Ϣ


�ͻ��ˣ�
org.androidpn.client.Constants  ���3������ XmppManager xmppManager  ����������ȡ���ӷ��ͻ�ִ��
org.androidpn.client.NotificationDetailsActivity  �鿴֪ͨ�Ľ��棬���˽��漤��ʱ�����Ͳ鿴��ִ�������  packetId��notificationFrom ͨ����2������������ȷ�������֪ͨ��������֤�ظ���
org.androidpn.client.NotificationIQProvider  ����
org.androidpn.client.NotificationPacketListener   ֪ͨ�������յ�֪ͨʱ�����ͽ��ջ�ִ�������
org.androidpn.client.NotificationReceiver  ����notificationFrom��packetId
		    .NotificationService   �洢ȫ�ֱ���Constants.xmppManager = xmppManager;
		    .Notifier              ����notificationFrom��packetId

�ǵý�androidpn.properties,xmppHost=192.168.7.233   �޸ĳ��Լ���IP


����ˣ�
hibernate.cfg.xml    ���һ��<mapping class="org.androidpn.server.model.NotificationMO" />
spring-config.xml    ���һ��service ,һ��dao
org.androidpn.server.console.controller.NotificationController  ���һ���������û�������Ϣ�Ĺ��ܣ���ԭ���ĸ����û������͸ĳ��˿�ָ������û�����
org.androidpn.server.dao.hibernate.NotificationDaoHibernate    ֪ͨ���ݿ����DAOʵ����
org.androidpn.server.dao.NotificationDao	�ӿ�
org.androidpn.server.model.NotificationMO   ֪ͨʵ�����  ����֪ͨ��Ϣ  ����ʱ�Զ������ݿ⽨һ��apn_notification��
		          .ReportVO	    ��ʱ����
org.androidpn.server.service.impl.NotificationServiceImpl	    ֪ͨ���ݿ����SERVICE
org.androidpn.server.service.NotificationService	�ӿ�
org.androidpn.server.service.ServiceLocator		ע��notificationService
org.androidpn.server.util.CopyMessageUtil		����������ϢID ���Ƹ�֪ͨ����
org.androidpn.server.xmpp.net.StanzaHandler		�û�����ʱ����������Ϣ
org.androidpn.server.xmpp.push.NotificationManager      ��Ϣ������ ���Ķ��Ƚϴ����Ӽ�������������������
org.androidpn.server.xmpp.router.IQRouter		������Ϣ���յ���ִ��Ϣ�޸����ݿ�״̬
WebRoot\WEB-INF\pages\notification\form.jsp		�Ӹ�����

