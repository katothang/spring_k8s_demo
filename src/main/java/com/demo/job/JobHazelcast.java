package com.demo.job;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

@Component
public class JobHazelcast implements HazelcastInstanceAware {
    @Autowired
    HazelcastInstance hazelcastInstance;

    private static final String LEADER_MAP_KEY = "leader";

    @Scheduled(fixedRate = 5000) // Chạy sau mỗi 5 giây
    public void printLeaderStatus() {
        IMap<String, String> leaderMap = hazelcastInstance.getMap(LEADER_MAP_KEY);
        String instanceId = hazelcastInstance.getLocalEndpoint().getUuid().toString();

        // Tạo một custom Entry Processor
        SetLeaderEntryProcessor entryProcessor = new SetLeaderEntryProcessor(instanceId);

        // Thực hiện đặt giá trị cho key "leader" và nhận kết quả trả về
        boolean isLeader = leaderMap.executeOnKey(LEADER_MAP_KEY, entryProcessor);

        if (isLeader) {
            System.out.println("Tôi là leader!");
        } else {
            System.out.println("Tôi là thành viên");
        }
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
    }

    // Custom Entry Processor để đặt giá trị cho key "leader"
    private static class SetLeaderEntryProcessor implements EntryProcessor<String, String, Boolean>, Serializable {
        private final String instanceId;

        public SetLeaderEntryProcessor(String instanceId) {
            this.instanceId = instanceId;
        }

        @Override
        public Boolean process(Map.Entry<String, String> entry) {
            // Kiểm tra nếu key "leader" chưa có giá trị, hoặc giá trị là instanceId, thì đặt giá trị mới
            if (entry.getValue() == null || entry.getValue().equals(instanceId)) {
                entry.setValue(instanceId);
                return true; // Trả về true nếu đặt giá trị thành công, chỉ có một instance làm leader
            }
            return false; // Trả về false nếu không đặt giá trị thành công
        }

        @Override
        public EntryProcessor<String, String, Boolean> getBackupProcessor() {
            return null;
        }

        public void processBackup(Map.Entry<String, String> entry) {
            // Không cần xử lý sao lưu trong trường hợp này
        }
    }
}
