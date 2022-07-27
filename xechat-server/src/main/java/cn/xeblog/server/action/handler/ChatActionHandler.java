package cn.xeblog.server.action.handler;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.util.BaiDuFyUtil;
import cn.xeblog.server.util.SensitiveWordUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@Slf4j
@DoAction(Action.CHAT)
public class ChatActionHandler extends AbstractActionHandler<UserMsgDTO> {

    private static final long MAX_SIZE = Convert.toLong(0.5 * 1024 * 1024);

    /**
     * 5分钟过期
     */
    private static final TimedCache<String, String> TIMED_CACHE = CacheUtil.newTimedCache(300000);

    private static final String SHUT_UP_COMMAND = "大封印术：封印鱼塘";

    private static final String SAY_COMMAND = "大封印术：解封鱼塘";

    /**
     * 是否闭嘴
     */
    private boolean isShutUp = false;

    @Override
    protected void process(User user, UserMsgDTO body) {
        final boolean isAdmin = User.Role.ADMIN == user.getRole();
        if (!isAdmin && isShutUp) {
            if (body.getMsgType() == UserMsgDTO.MsgType.TEXT) {
                log.info("封印鱼塘，{} 被拒绝发言：{}", user.getUsername(), Convert.toStr(body.getContent()));
            }
            user.send(ResponseBuilder.build(null, "鱼塘已封印", MessageType.SYSTEM));
            return;
        }
        if (body.getMsgType() == UserMsgDTO.MsgType.TEXT) {
            String msg = Convert.toStr(body.getContent());
            if (isAdmin && StrUtil.equals(SHUT_UP_COMMAND, msg)) {
                isShutUp = true;
            }
            if (isAdmin && StrUtil.equals(SAY_COMMAND, msg)) {
                isShutUp = false;
            }
            BaiDuFyUtil baiDuFyUtil = Singleton.get(BaiDuFyUtil.class.getName(), () -> new BaiDuFyUtil("", ""));
            body.setContent(baiDuFyUtil.translate(SensitiveWordUtils.loveChina(msg)));
        } else if (body.getMsgType() == UserMsgDTO.MsgType.IMAGE) {
            byte[] bytes = (byte[]) body.getContent();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
            int fileNameLength = byteBuf.readInt();
            String fileName = new String(ByteBufUtil.getBytes(byteBuf.readBytes(fileNameLength)));
            int fileLength = byteBuf.readInt();
            byteBuf.release();
            if (fileLength > MAX_SIZE) {
                final String fileSize = FileUtil.readableFileSize(MAX_SIZE);
                if (StrUtil.isBlank(TIMED_CACHE.get(fileName))) {
                    final String uploadFileSize = FileUtil.readableFileSize(fileLength);
                    // 缓存中没有记录就不提示了,没有才提示
                    user.send(ResponseBuilder.build(null, StrUtil.format("图片大小不能超过 {}！", fileSize), MessageType.SYSTEM));
                    log.info("{} 上传图片 {} 为 {} 超过 {}，拒绝分发....", user.getUsername(), fileName, uploadFileSize, fileSize);
                    TIMED_CACHE.put(fileName, fileName);
                }
                return;
            }
        }
        ChannelAction.send(user, body, MessageType.USER);
    }

}
