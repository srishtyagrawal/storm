/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.metric.cgroup;

import java.util.Map;

import org.apache.storm.container.cgroup.SubSystemType;
import org.apache.storm.container.cgroup.core.CgroupCore;
import org.apache.storm.container.cgroup.core.MemoryCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reports the current memory limit of the cgroup for this worker
 */
public class CGroupMemoryLimit extends CGroupMetricsBase<Long> {
    private static final Logger LOG = LoggerFactory.getLogger(CGroupMemoryLimit.class);
    private final long _workerLimitBytes;
    private static final long BYTES_PER_MB = 1024 * 1024;

    public CGroupMemoryLimit(Map<String, Object> conf) {
        super(conf, SubSystemType.memory);
        //In some cases we might be limiting memory in the supervisor and not in the cgroups
        long limit = -1;
        try {
            limit = Long.valueOf( System.getProperty("worker.memory_limit_mb", "-1"));
        } catch (NumberFormatException e) {
            LOG.warn("Error Parsing worker.memory_limit_mb {}", e);
        }
        _workerLimitBytes = BYTES_PER_MB * limit;
    }

    @Override
    public Long getDataFrom(CgroupCore core) throws Exception {
        if (_workerLimitBytes > 0) {
            return _workerLimitBytes;
        }
        return ((MemoryCore) core).getPhysicalUsageLimit();
    }
}
